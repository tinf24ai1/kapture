package com.kapture.kapture.reminder

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.WorkerParameters
import com.kapture.kapture.notifications.NotificationService
import com.kapture.kapture.settings.AndroidContextHolder
import com.kapture.kapture.storage.Item
import kotlinx.datetime.*
import java.util.concurrent.TimeUnit
import com.kapture.kapture.logger.Logger


private const val NOTIF_TITLE = "A new time capsule is ready!"
private const val NOTIF_MESSAGE = "Check it out in your Kapture"

class AndroidReminderScheduler(
    private val ctx: Context
) : ReminderScheduler {

    override fun schedule(item: Item, hour: Int, minute: Int) {
        // Convert LocalDate + hh:mm to Instant
        val triggerAt = item.releaseDate
            .atTime(hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
        val now = Clock.System.now()
        val delayMs = (triggerAt.toEpochMilliseconds() - now.toEpochMilliseconds()).coerceAtLeast(0L)

        val hm = "${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"

        val request = OneTimeWorkRequestBuilder<ShowIdeaNotificationWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "title" to NOTIF_TITLE,
                    "body"  to NOTIF_MESSAGE
                )
            )
            .addTag(itemTag(itemId = item.title))
            .build()

        // Keep only one pending reminder – replace previous
        WorkManager.getInstance(ctx).enqueueUniqueWork(
            uniqueName(item.title),
            ExistingWorkPolicy.REPLACE,
            request
        )

        Logger.i(
            "Reminder",
            "Android plans '${item.title}' für ${item.releaseDate} %02d:%02d (delayMs=%d)"
                .format(hour, minute, delayMs)
        )
    }

    override fun cancel(itemId: String) {
        WorkManager.getInstance(ctx).cancelAllWorkByTag(itemTag(itemId))
    }

    private fun uniqueName(itemId: String) = "idea-$itemId"
    private fun itemTag(itemId: String) = "idea-tag-$itemId"
}

// Worker run by WorkManager for scheduled time (inexact because of Doze/Standby)
class ShowIdeaNotificationWorker(
    appCtx: Context,
    params: WorkerParameters
) : CoroutineWorker(appCtx, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val title = inputData.getString("title")
            ?: "A new time capsule is ready!"
        val body  = inputData.getString("body")
            ?: "Check it out in your Kapture"

        Logger.i("Reminder", "Worker START – title='$title'")

        // Use application context (works when process launched in background)
        val ctx = applicationContext

        val enabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
        if (!enabled) {
            Logger.i("Reminder", "Worker: notifications disabled - skip")
            return Result.success()
        }

        val svc = NotificationService().also { it.setAppContext(ctx) }
        svc.showNotification(title, body)

        Logger.i("Reminder", "Worker DONE – notification shown")
        return Result.success()
    }
}

actual fun createReminderScheduler(): ReminderScheduler =
    AndroidReminderScheduler(AndroidContextHolder.appContext)
