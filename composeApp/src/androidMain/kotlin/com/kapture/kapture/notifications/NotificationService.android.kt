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

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)       // <- wichtig für < API 26
            .setDefaults(NotificationCompat.DEFAULT_ALL)         // Sound/Vibration/Lights nach User-Settings
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)    // optional, hilft bei Heads-Up
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
                // Ergebnis kommt in MainActivity.onRequestPermissionsResult
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
