package com.kapture.kapture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.content.pm.PackageManager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.kapture.kapture.notifications.NotificationStateEvent
import com.kapture.kapture.notifications.NotificationPermissionType
import com.kapture.kapture.notifications.NotificationService
import com.kapture.kapture.notifications.AppViewModel
import com.kapture.kapture.notifications.PlatformActivity

class MainActivity : ComponentActivity() {

    private val notificationService by lazy { NotificationService() }
    private val notificationVm by lazy { AppViewModel(notificationService) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                onRefreshFabClick = {
                    // 1) Permission sicherstellen (liefert auch AppContext für Android)
                    notificationVm.askNotificationPermission(PlatformActivity(this))
                    // 2) Sofort Notification zeigen
                    notificationVm.showNotification()
                }
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationService.REQUEST_CODE_NOTIFICATIONS) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
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