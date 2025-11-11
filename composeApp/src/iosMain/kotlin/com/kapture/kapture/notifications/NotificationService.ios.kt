package com.kapture.kapture.notifications

import com.kapture.kapture.logger.Logger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.*

actual class NotificationService actual constructor() {

    private val TAG = "Notifications"

    actual fun showNotification(title: String, message: String?) {
        Logger.i(TAG, "Posting iOS notification (in ~1s) title=\"$title\"")
        scheduleInternal(
            id = "now-${(NSDate().timeIntervalSince1970 * 1000).toLong()}",
            title = title,
            body = message ?: "",
            seconds = 1.0
        )
    }

    actual fun requestPermission(activity: PlatformActivity?, onFinished: (Boolean) -> Unit) {
        Logger.i(TAG, "Requesting iOS notification authorization...")
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, _ ->
            Logger.i(TAG, "iOS user answered permission: granted=$granted")
            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED
            )
            onFinished(granted)
        }
    }

    actual suspend fun areNotificationsEnabled(): Boolean =
        suspendCancellableCoroutine { cont ->
            UNUserNotificationCenter.currentNotificationCenter()
                .getNotificationSettingsWithCompletionHandler { settings ->
                    val status = settings?.authorizationStatus
                    val enabled = (status == UNAuthorizationStatusAuthorized) ||
                            (status == UNAuthorizationStatusProvisional)
                    Logger.d(TAG, "iOS areNotificationsEnabled() = $enabled (status=$status)")
                    if (cont.isActive) cont.resume(enabled)
                }
        }

    private fun scheduleInternal(id: String, title: String, body: String, seconds: Double) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
        }
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(seconds, false)
        val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)
        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request, withCompletionHandler = null)
    }

    actual companion object {
        actual val REQUEST_CODE_NOTIFICATIONS: Int get() = 0
    }
}
