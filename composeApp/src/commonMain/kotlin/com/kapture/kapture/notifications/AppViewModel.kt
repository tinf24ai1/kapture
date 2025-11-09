package com.kapture.kapture.notifications

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest

class AppViewModel(
    private val notificationService: NotificationService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _notificationEnabledState = MutableStateFlow(false)
    val notificationEnabledState = _notificationEnabledState.asStateFlow()

    init {
        scope.launch {
            _notificationEnabledState.value = notificationService.areNotificationsEnabled()
        }
        scope.launch {
            NotificationStateEvent.observe().collectLatest {
                _notificationEnabledState.value = (it == NotificationPermissionType.GRANTED)
            }
        }
    }

    fun askNotificationPermission(activity: PlatformActivity?) {
        notificationService.requestPermission(activity) { granted ->
            // Falls die Plattform kein Event sendet, setzen wir es hier
            NotificationStateEvent.send(
                if (granted) NotificationPermissionType.GRANTED else NotificationPermissionType.DENIED
            )
        }
    }

    fun showNotification() {
        notificationService.showNotification(
            title = "Test",
            message = "Test Test Test"
        )
    }
}
