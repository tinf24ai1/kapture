package com.kapture.kapture.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.reminder.IdeaAlarmReceiver.Companion.EXTRA_BODY
import com.kapture.kapture.reminder.IdeaAlarmReceiver.Companion.EXTRA_ITEM_ID
import com.kapture.kapture.reminder.IdeaAlarmReceiver.Companion.EXTRA_TITLE
import com.kapture.kapture.settings.AndroidContextHolder
import com.kapture.kapture.storage.ItemModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.Calendar

private const val NOTIF_TITLE = "A new time capsule is ready!"
private const val NOTIF_MESSAGE = "Check it out in your Kapture"

class AndroidReminderScheduler(
    private val ctx: Context
) : ReminderScheduler {

    private val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(itemModel: ItemModel, hour: Int, minute: Int) {
        val triggerAtMillis = computeTriggerMillis(itemModel, hour, minute)

        val pi = pendingIntentFor(itemModel, title = NOTIF_TITLE, body = NOTIF_MESSAGE)

        alarmManager.cancel(pi)

        // Exact alarms if possible
        val canExact = Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()

        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }

        Logger.i("Reminder", "Scheduled itemId=${itemModel.id} at=$triggerAtMillis exact=$canExact")
    }

    override fun cancel(itemId: String) {
        val pi = PendingIntent.getBroadcast(
            ctx,
            itemId.hashCode(),
            Intent(ctx, IdeaAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return

        alarmManager.cancel(pi)
        pi.cancel()

        Logger.i("Reminder", "Cancelled itemId=$itemId")
    }

    private fun pendingIntentFor(itemModel: ItemModel, title: String, body: String): PendingIntent {
        val intent = Intent(ctx, IdeaAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ITEM_ID, itemModel.id)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
        }

        return PendingIntent.getBroadcast(
            ctx,
            itemModel.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // If trigger time in past (timeskip) -> Notify after 1sec
    private fun computeTriggerMillis(itemModel: ItemModel, hour: Int, minute: Int): Long {
        val nowDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, itemModel.releaseDate.year)
            set(Calendar.MONTH, itemModel.releaseDate.monthNumber - 1) // Calendar months: 0..11
            set(Calendar.DAY_OF_MONTH, itemModel.releaseDate.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val target = cal.timeInMillis
        val now = System.currentTimeMillis()

        return if (itemModel.releaseDate < nowDate || target < now) {
            now + 1_000L
        } else {
            target
        }
    }
}

actual fun createReminderScheduler(): ReminderScheduler =
    AndroidReminderScheduler(AndroidContextHolder.appContext)
