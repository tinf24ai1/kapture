package com.kapture.kapture

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.kapture.kapture.notifications.AppViewModel
import com.kapture.kapture.notifications.NotificationService
import androidx.compose.runtime.*


fun MainViewController() = ComposeUIViewController {
    val vm = AppViewModel(NotificationService())

    // Ask for permission on startup
    LaunchedEffect(Unit) {
        vm.askNotificationPermission(activity = null, source = "startup")
    }

    // Observe dialog flag from ViewModel
    val showDenied by vm.showDeniedDialog.collectAsState(initial = false)

    App()
}
