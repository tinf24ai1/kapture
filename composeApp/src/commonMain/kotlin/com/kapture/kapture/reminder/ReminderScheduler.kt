package com.kapture.kapture.reminder

import com.kapture.kapture.storage.ItemModel

// Interface to plan reminders
// Idea: only plan the next notification
interface ReminderScheduler {
    fun schedule(itemModel: ItemModel, hour: Int = 10, minute: Int = 0)
    fun cancel(itemId: String)
}
