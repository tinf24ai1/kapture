package com.kapture.kapture.reminder

import android.content.Context
import androidx.work.*
import androidx.work.WorkerParameters
import com.kapture.kapture.notifications.NotificationService
import com.kapture.kapture.settings.AndroidContextHolder
import com.kapture.kapture.storage.Item
import kotlinx.datetime.*
import java.util.concurrent.TimeUnit
import com.kapture.kapture.logger.Logger


/** Android-Implementation: OneTimeWorkRequest (WorkManager), „ungefähr zur Zeit X“. */
class AndroidReminderScheduler(
    private val ctx: Context
) : ReminderScheduler {

    override fun schedule(item: Item, hour: Int, minute: Int) {
        val triggerAt = item.releaseDate
            .atTime(hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
        val now = Clock.System.now()
        val delayMs = (triggerAt.toEpochMilliseconds() - now.toEpochMilliseconds()).coerceAtLeast(0L)

        val request = OneTimeWorkRequestBuilder<ShowIdeaNotificationWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    // WICHTIG: Nutze eine stabile ID – falls Item noch keine hat, nimm vorerst title.
                    "itemId" to item.title,
                    "title" to "Idea ist reif",
                    "body" to item.title
                )
            )
            .addTag(itemTag(itemId = item.title))
            .build()

        WorkManager.getInstance(ctx).enqueueUniqueWork(
            uniqueName(item.title),
            ExistingWorkPolicy.REPLACE,
            request
        )

        Logger.i(
            "Reminder",
            "Android plant '${item.title}' für ${item.releaseDate} %02d:%02d (delayMs=%d)"
                .format(hour, minute, delayMs)
        )
    }

    override fun cancel(itemId: String) {
        WorkManager.getInstance(ctx).cancelAllWorkByTag(itemTag(itemId))
    }

    private fun uniqueName(itemId: String) = "idea-$itemId"
    private fun itemTag(itemId: String) = "idea-tag-$itemId"
}

/** Worker: zeigt zur geplanten Zeit die Benachrichtigung. */
class ShowIdeaNotificationWorker(
    appCtx: Context,
    params: WorkerParameters
) : CoroutineWorker(appCtx, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: return Result.failure()
        val body = inputData.getString("body") ?: ""

        // Nutzt eure bestehende NotificationService-Implementation
        NotificationService().showNotification(title, body)

        // Optional: Nachfeuer-„Kette“ für Android (ohne App-Open).
        // -> Kannst du aktivieren, wenn du den Heap im Hintergrund laden willst.
        // createReminderScheduler().scheduleNext( ... ) // nur sinnvoll, wenn du hier Zugriff auf Items hast.
        Logger.i("Reminder", "Worker ausgeführt – zeige Notification für '${body}'.")

        return Result.success()
    }
}

/** Factory-actual */
actual fun createReminderScheduler(): ReminderScheduler =
    AndroidReminderScheduler(AndroidContextHolder.appContext)
