package com.kapture.kapture.notifications

import platform.UserNotifications.*
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.native.concurrent.AtomicInt

actual class NotificationService actual constructor() {

    actual fun showNotification(title: String, message: String?) {
        // "Sofort" = Trigger in ~1 Sekunde
        scheduleInternal(
            id = "now-${(NSDate().timeIntervalSince1970 * 1000).toLong()}",
            title = title,
            body = message ?: "",
            seconds = 1.0
        )
    }

    actual fun requestPermission(activity: PlatformActivity?, onFinished: (Boolean) -> Unit) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, _ ->
            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED
            )
            onFinished(granted)
        }
    }

    actual suspend fun areNotificationsEnabled(): Boolean {
        var result = false
        val done = AtomicInt(0)
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                result = (settings?.authorizationStatus == UNAuthorizationStatusAuthorized) ||
                        (settings?.authorizationStatus == UNAuthorizationStatusProvisional)
                done.value = 1
            }
        while (done.value == 0) { /* simple wait for demo */ }
        return result
    }

    private fun scheduleInternal(id: String, title: String, body: String, seconds: Double) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title); setBody(body)
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
