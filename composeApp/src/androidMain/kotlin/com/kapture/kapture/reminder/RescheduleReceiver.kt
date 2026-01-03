package com.kapture.kapture.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.storage.ItemModel
import com.kapture.kapture.storage.LocalItemRepository
import com.kapture.kapture.storage.LocalStorage


class RescheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("Reminder", "RescheduleReceiver action=${intent.action}")

        // Load heap
        val itemModels: List<ItemModel> = LocalItemRepository.load()

        val scheduler = AndroidReminderScheduler(context.applicationContext)

        // Plan all items again at 10:00
        for (item in itemModels) {
            scheduler.cancel(item.id)
            scheduler.schedule(item, hour = 10, minute = 0)
        }
    }
}