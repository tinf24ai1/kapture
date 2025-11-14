package com.kapture.kapture.reminder

import com.kapture.kapture.storage.Item
import platform.Foundation.NSDateComponents
import platform.UserNotifications.*
import com.kapture.kapture.logger.Logger


private const val NOTIF_TITLE = "A new time capsule is ready!"
private const val NOTIF_MESSAGE = "Check it out in your Kapture"
class IOSReminderScheduler : ReminderScheduler {

    override fun schedule(item: Item, hour: Int, minute: Int) {
        // Build NSDateComponents from LocalDate + hh:mm
        val d = item.releaseDate
        val comps = NSDateComponents().apply {
            year = d.year.toLong()
            month = d.monthNumber.toLong()
            day = d.dayOfMonth.toLong()
            this.hour = hour.toLong()
            this.minute = minute.toLong()
        }

        // iOS fires approximately; OS may coalesce under power constraints
        val content = UNMutableNotificationContent().apply {
            NOTIF_TITLE
            NOTIF_MESSAGE
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = comps,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = item.title,
            content = content,
            trigger = trigger
        )


        val hm = "${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"

        Logger.i(
            "Reminder",
            "iOS plant '${item.title}' für ${item.releaseDate} $hm"

        )

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request, withCompletionHandler = null)
    }

    override fun cancel(itemId: String) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(itemId))
    }
}

/** Factory-actual */
actual fun createReminderScheduler(): ReminderScheduler = IOSReminderScheduler()
