package com.kapture.kapture

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.kapture.kapture.notifications.AppViewModel
import com.kapture.kapture.notifications.NotificationService

fun MainViewController() = ComposeUIViewController {
    val vm = AppViewModel(NotificationService())

    // Option A: bei JEDEM Start Permission-Check anstoßen (Dialog erscheint nur 1x systemseitig)
    LaunchedEffect(Unit) {
        vm.askNotificationPermission(activity = null, source = "startup")
    }

    App(
        onRefreshFabClick = {
            vm.sendWithPermission(
                activity = null,
                title = "Test",
                message = "Test Test Test"
            )
        }
    )
}
