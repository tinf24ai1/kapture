package com.kapture.kapture.notifications

import com.kapture.kapture.logger.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

// Kleiner Event-Bus: ViewModel kann Permission-Änderungen reaktiv beobachten
object NotificationStateEvent {
    private const val TAG = "Notifications"

    private val _event = Channel<NotificationPermissionType>(capacity = Channel.BUFFERED)
    fun observe() = _event.receiveAsFlow()
    fun send(event: NotificationPermissionType) {
        Logger.i(TAG, "Permission event sent: $event")
        _event.trySend(event)
    }
}

enum class NotificationPermissionType { GRANTED, DENIED }
