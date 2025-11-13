package com.kapture.kapture.reminder

import com.kapture.kapture.logger.Logger
import com.kapture.kapture.storage.Item

// Logs "who scheduled" & "what is next", then delegates platform scheduler
// Only next due idea is scheduled at a time.
fun ReminderScheduler.scheduleNextWithLog(
    source: String,
    itemsSortedByDate: List<Item>,
    hour: Int = 10,
    minute: Int = 0
) {
    if (itemsSortedByDate.isEmpty()) {
        Logger.i("Reminder", "[$source] No items - nothing to schedule.")
        return
    }
    val head = itemsSortedByDate.first()
    val hm = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

    Logger.i(
        "Reminder",
        "[$source] plan next Idea: '${head.title}' am ${head.releaseDate} um $hm"
    )
    //Platform-specific scheduling
    schedule(head, hour, minute)
}
