package com.kapture.kapture.reminder

import com.kapture.kapture.logger.Logger
import com.kapture.kapture.storage.Item

/**
 * Loggt zentral (Quelle, Item, Datum/Uhrzeit) und plant dann genau die nächste Idee.
 */
fun ReminderScheduler.scheduleNextWithLog(
    source: String,
    itemsSortedByDate: List<Item>,
    hour: Int = 10,
    minute: Int = 0
) {
    if (itemsSortedByDate.isEmpty()) {
        Logger.i("Reminder", "[$source] Keine Items – nichts zu planen.")
        return
    }
    val head = itemsSortedByDate.first()
    Logger.i(
        "Reminder",
        "[$source] plane NÄCHSTE Idee: '${head.title}' am ${head.releaseDate} um %02d:%02d"
            .format(hour, minute)
    )
    schedule(head, hour, minute)
}
