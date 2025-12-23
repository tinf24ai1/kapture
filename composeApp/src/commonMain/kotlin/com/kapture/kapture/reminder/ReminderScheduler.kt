package com.kapture.kapture.reminder

import com.kapture.kapture.storage.Item

// Interface to plan reminders
// Idea: only plan the next notification
interface ReminderScheduler {

    fun schedule(item: Item, hour: Int = 10, minute: Int = 0)

    fun cancel(itemId: String)


}
