package com.kapture.kapture.notifications

import com.kapture.kapture.logger.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import com.kapture.kapture.settings.AppSettings

// Platform-agnostic ViewModel for notification permission handling and sending notifications.

class AppViewModel(
    private val notificationService: NotificationService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val TAG = "Notifications"

    //UI observable: True when OS allows notifications
    private val _notificationEnabledState = MutableStateFlow(false)
    val notificationEnabledState = _notificationEnabledState.asStateFlow()

    // Pending payload used when sending notification is requested but not granted
    private val _showDeniedDialog = MutableStateFlow(false)
    val showDeniedDialog = _showDeniedDialog.asStateFlow()

    // Optional UX dialog when user denies
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

                if (!granted) {
                    if (!AppSettings.wasDenialHintShown()) {
                        _showDeniedDialog.value = true
                        AppSettings.setDenialHintShown(true)
                    } else {
                        Logger.d(TAG, "Denial hint already shown before - skipping dialog")
                    }
                }

                if (granted && pendingTitle != null) {
                    val t = pendingTitle
                    val m = pendingMessage
                    pendingTitle = null
                    pendingMessage = null

                    if (t != null) {
                        Logger.i(TAG, "Permission granted - sending pending notification (title=\"$t\")")
                        notificationService.showNotification(title = t, message = m)
                    }
                }
            }
        }
    }

    fun askNotificationPermission(activity: PlatformActivity?, source: String = "manual") {
        Logger.i(TAG, "Requesting notification permission (source=$source)...")
        notificationService.requestPermission(activity) { granted ->
            Logger.i(TAG, "Permission callback result (source=$source): granted=$granted")
            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED
            )
        }
    }

    fun dismissDeniedDialog() {
        _showDeniedDialog.value = false
    }
}
