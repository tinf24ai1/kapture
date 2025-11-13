package com.kapture.kapture.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kapture.kapture.logger.Logger
import android.app.PendingIntent
import android.content.Intent
import com.kapture.kapture.MainActivity

// Single channel definition (ID for high importance)
private const val CHANNEL_ID   = "Kapture_reminders_v2"
private const val CHANNEL_NAME = "Kapture Reminders"
private const val CHANNEL_DESC = "Notification when a time capsule is ready to be opened"

private fun ensureChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val existing = nm.getNotificationChannel(CHANNEL_ID)
        if (existing == null) {
            val ch = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            nm.createNotificationChannel(ch)
            Logger.i("Reminder", "Channel created: id=$CHANNEL_ID, importance=HIGH")
        } else {
            Logger.i("Reminder", "Channel exists: id=$CHANNEL_ID, importance=${existing.importance}")
        }
    }
}


//Android implementation of NotificationService using NotificationCompat and runtime permission
actual class NotificationService actual constructor() {

    private var appContext: Context? = null
    private val TAG = "Notifications"

    //Set worker/Activity
    fun setAppContext(ctx: Context) {
        appContext = ctx.applicationContext
    }

    //Called from UI or from the worker
    actual fun showNotification(title: String, message: String?) {
        val ctx = appContext ?: run {
            Logger.i("Reminder", "No appContext set – call setAppContext(...) before showNotification")
            return
        }


        ensureChannel(ctx)

        val enabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
        if (!enabled) {
            Logger.i("Reminder", "Notifications disabled at app-level → not showing")
            return
        }
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            val ch = nm.getNotificationChannel(CHANNEL_ID)
            if (ch != null && ch.importance == NotificationManager.IMPORTANCE_NONE) {
                Logger.i("Reminder", "Channel is BLOCKED by user → skipping (consider openChannelSettings(ctx))")
                return
            }
        }

        // Banner tap - opens app
        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("fromNotification", true)
            putExtra("title", title)
            putExtra("message", message)
        }
        val pi = PendingIntent.getActivity(
            ctx,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message ?: "")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // später: eigenes Icon
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // < API 26
            .build()

        NotificationManagerCompat.from(ctx).notify(title.hashCode(), notif)
        Logger.i("Reminder", "notify() issued on channel=$CHANNEL_ID title='$title'")
    }

    actual fun requestPermission(activity: PlatformActivity?, onFinished: (Boolean) -> Unit) {
        val act = activity?.activity ?: run {
            onFinished(false); return
        }

        appContext = act.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                act, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                Logger.i(TAG, "Android permission already granted")
                NotificationStateEvent.send(NotificationPermissionType.GRANTED)
                onFinished(true)
            } else {
                Logger.i(TAG, "Requesting Android POST_NOTIFICATIONS permission (SDK>=33)")
                ActivityCompat.requestPermissions(
                    act,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE
                )
                // Result via MainActivity.onRequestPermissionsResult
            }
        } else {
            NotificationStateEvent.send(NotificationPermissionType.GRANTED)
            onFinished(true)
        }
    }

    actual suspend fun areNotificationsEnabled(): Boolean {
        val ctx = appContext ?: return false
        val enabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled()
        return enabled
    }

    actual companion object {
        actual val REQUEST_CODE_NOTIFICATIONS: Int get() = REQUEST_CODE
        private const val REQUEST_CODE = 1001

    }
}
