package com.kapture.kapture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.content.pm.PackageManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.notifications.NotificationStateEvent
import com.kapture.kapture.notifications.NotificationPermissionType
import com.kapture.kapture.notifications.NotificationService
import com.kapture.kapture.notifications.AppViewModel
import com.kapture.kapture.notifications.PlatformActivity
import com.kapture.kapture.settings.AndroidContextHolder

class MainActivity : ComponentActivity() {

    private val notificationService by lazy { NotificationService() }
    private val notificationVm by lazy { AppViewModel(notificationService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        AndroidContextHolder.appContext = applicationContext

        setContent {

            // Ask permission on startup
            LaunchedEffect(Unit) {
                notificationVm.askNotificationPermission(
                    activity = PlatformActivity(this@MainActivity),
                    source = "startup"
                )
            }

            // Collect Dialog-Flag from ViewModel
            val showDenied by notificationVm.showDeniedDialog.collectAsState(initial = false)

            App(
                showPermissionHintDialog = showDenied,
                onDismissPermissionHint = { notificationVm.dismissDeniedDialog() },
            )
        }
    }

    //Forward Android's permission to shared event bus
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationService.REQUEST_CODE_NOTIFICATIONS) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            Logger.i("Notifications", "Android user answered permission: granted=$granted")


            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED
                        else NotificationPermissionType.DENIED
            )
        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}