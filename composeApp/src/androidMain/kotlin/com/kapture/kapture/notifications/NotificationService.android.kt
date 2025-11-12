package com.kapture.kapture.notifications

import android.Manifest
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

//Android implementation of NotificationService using NotificationCompat and runtime permission
actual class NotificationService actual constructor() {

    private var appContext: Context? = null
    private val TAG = "Notifications"

    actual fun showNotification(title: String, message: String?) {
        val ctx = appContext
        if (ctx == null) {
            return
        }
        ensureChannel(ctx)

        var id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        Logger.i(TAG, "Posting Android notification id=$id title=\"$title\"")

        // Intent to open MainActivity when tapping banner
        val openIntent = Intent(ctx, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("from_notification", true) // for future deep-link handling
        }

        val flags = (PendingIntent.FLAG_UPDATE_CURRENT) or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

        val contentPendingIntent = PendingIntent.getActivity(
            ctx, /*requestCode=*/ id, openIntent, flags
        )

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(contentPendingIntent)
            .build()

        NotificationManagerCompat.from(ctx).notify(id, notification)

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
        private const val CHANNEL_ID = "default_high"

        //Create high-importance channel
        private fun ensureChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val chan = NotificationChannel(
                    CHANNEL_ID,
                    "Default",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    enableVibration(true)
                }
                nm.createNotificationChannel(chan)
            }
        }
    }
}
