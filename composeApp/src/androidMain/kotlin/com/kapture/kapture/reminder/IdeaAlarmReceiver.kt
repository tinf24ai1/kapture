package com.kapture.kapture.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.notifications.NotificationService
import com.kapture.kapture.storage.HeapUpdate
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.LocalStorage

class IdeaAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "A new time capsule is ready!"
        val body  = intent.getStringExtra(EXTRA_BODY)  ?: "Check it out in your Kapture"
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID) ?: "unknown"

        // Check if already notified
        val items: List<Item> = LocalStorage.restore("MinHeap") ?: emptyList()
        val item = items.firstOrNull { it.id == itemId } ?: return

        if (item.notified) {
            Logger.i("Reminder", "Skip: already notified itemId=$itemId")
            return
        }


        Logger.i("Reminder", "Alarm fired for itemId=$itemId")

        // Use NotificationService (Channel+Compat Builder)
        val hasNotifPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasNotifPermission) {
            try {
        NotificationService()
            .also { it.setAppContext(context.applicationContext) }
            .showNotification(title, body)
            } catch (e: SecurityException) {
                Logger.i("Reminder", "Failed to show notification: ${e.message}")
            }
            // Mark as notified
            HeapUpdate.markNotified(itemId)
        } else {
            Logger.i("Reminder", "POST_NOTIFICATIONS permission not granted; skipping notification")
        }
    }

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }
}