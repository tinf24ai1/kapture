package com.kapture.kapture.reminder

import com.kapture.kapture.storage.Item
import platform.Foundation.NSDateComponents
import platform.UserNotifications.*
import com.kapture.kapture.logger.Logger


/** iOS-Implementation: UNCalendarNotificationTrigger, „ungefähr“. */
class IOSReminderScheduler : ReminderScheduler {

    override fun schedule(item: Item, hour: Int, minute: Int) {
        val d = item.releaseDate
        val comps = NSDateComponents().apply {
            year = d.year.toLong()
            month = d.monthNumber.toLong()
            day = d.dayOfMonth.toLong()
            this.hour = hour.toLong()
            this.minute = minute.toLong()
        }

        val content = UNMutableNotificationContent().apply {
            title = "Idea ist reif"
            body = item.title
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = comps,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = item.title, // Besser: Item-ID, wenn vorhanden
            content = content,
            trigger = trigger
        )

        Logger.i(
            "Reminder",
            "iOS plant '${item.title}' für ${item.releaseDate} %02d:%02d"
                .format(hour, minute)
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
