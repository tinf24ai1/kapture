package com.kapture.kapture.notifications

import com.kapture.kapture.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

class AppViewModel(
    private val notificationService: NotificationService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "Notifications"

    private val _notificationEnabledState = MutableStateFlow(false)
    val notificationEnabledState = _notificationEnabledState.asStateFlow()

    // NEU: Merker für geplante Notification, die erst nach Grant gesendet werden soll
    private var pendingTitle: String? = null
    private var pendingMessage: String? = null

    init {
        scope.launch {
            val enabled = notificationService.areNotificationsEnabled()
            Logger.d(TAG, "areNotificationsEnabled() = $enabled")
            _notificationEnabledState.value = enabled
        }
        scope.launch {
            NotificationStateEvent.observe().collectLatest { ev ->
                Logger.i(TAG, "Permission event observed: $ev")
                val granted = (ev == NotificationPermissionType.GRANTED)
                _notificationEnabledState.value = granted

                // Wenn jetzt erlaubt und etwas „pending“ ist → JETZT senden
                if (granted && pendingTitle != null) {
                    val t = pendingTitle
                    val m = pendingMessage
                    pendingTitle = null
                    pendingMessage = null

                    if (t != null) {
                        Logger.i(TAG, "Permission granted → sending pending notification (title=\"$t\")")
                        // tatsächlicher Versand & Logging nur hier
                        notificationService.showNotification(title = t, message = m)
                    }
                }
            }
        }
    }


    /**
     * Option A: bei jedem Appstart aufrufen (Dialog erscheint nur beim allerersten Mal systemseitig).
     * source ist optional für Logs ("startup" | "manual" ...).
     */
    fun askNotificationPermission(activity: PlatformActivity?, source: String = "manual") {
        Logger.i(TAG, "Requesting notification permission (source=$source)...")
        notificationService.requestPermission(activity) { granted ->
            Logger.i(TAG, "Permission callback result (source=$source): granted=$granted")
            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED
            )
        }
    }

    /**
     * Öffentlicher API-Call für die UI:
     * - Wenn Permission vorhanden → sofort senden.
     * - Sonst: pending speichern, Permission anfragen, Versand passiert nach GRANTED-Event.
     */
    fun sendWithPermission(activity: PlatformActivity?, title: String, message: String? = null) {
        scope.launch {
            val enabled = notificationService.areNotificationsEnabled()
            if (enabled) {
                Logger.i(TAG, "Permission already granted → sending now (title=\"$title\")")
                notificationService.showNotification(title = title, message = message)
            } else {
                Logger.i(TAG, "Permission not granted → requesting; will queue pending notification (title=\"$title\")")
                pendingTitle = title
                pendingMessage = message
                // Wichtig: hier NICHT selbst senden; wir warten auf das Event
                notificationService.requestPermission(activity) { granted ->
                    // Wir loggen nur das Ergebnis; der eigentliche Versand passiert im Observer (oben),
                    // sobald das GRANTED-Event eintrifft (Android via Activity-Callback; iOS via completion)
                    Logger.i(TAG, "Permission callback result: granted=$granted")
                    if (granted) {
                        val t = pendingTitle
                        val m = pendingMessage
                        pendingTitle = null
                        pendingMessage = null
                        if (t != null) {
                            Logger.i(TAG, "Permission granted in callback → sending pending notification (title=\"$t\")")
                            notificationService.showNotification(title = t, message = m)

                        }
                    }
                    // Event bleibt trotzdem gut für State-UI:
                    NotificationStateEvent.send(
                        if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED)
                    // (Das Event wird bereits im Service gesendet; falls eine Plattform es nicht sendet,
                    // bleibt zusätzlich das Callback-Log hier sichtbar.)
                }
            }
        }
    }

    // Behalte diese einfache Methode, aber nutze sie intern nur, wenn gesichert erlaubt ist:
    fun showNotification(title: String, message: String? = null) {
        Logger.i(TAG, "Sending notification (title=\"$title\")")
        notificationService.showNotification(title = title, message = message)
    }
}
