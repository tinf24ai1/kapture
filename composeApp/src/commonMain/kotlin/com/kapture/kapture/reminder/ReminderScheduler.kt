package com.kapture.kapture.reminder

import com.kapture.kapture.storage.Item

/**
 * Plant Benachrichtigungen für Ideen.
 * Strategie: immer nur die NÄCHSTE Idee (frühestes Datum) planen.
 */
interface ReminderScheduler {
    /** Eine konkrete Idee für (hour:minute) am releaseDate planen. */
    fun schedule(item: Item, hour: Int = 10, minute: Int = 0)

    /** Eine konkret geplante Idee (per stabiler ID) stornieren. */
    fun cancel(itemId: String)

    /** Bequemer Helfer: nimmt sortierte Liste und plant nur das erste Element. */
    fun scheduleNext(itemsSortedByDate: List<Item>, hour: Int = 10, minute: Int = 0) {
        if (itemsSortedByDate.isEmpty()) return
        schedule(itemsSortedByDate.first(), hour, minute)
    }
}
