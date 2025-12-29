package com.kapture.kapture.notifications

// Crossplatfrom notification contract
expect class NotificationService() {

    fun showNotification(
        title: String,
        message: String? = null
    )

    // Request permission. Android needs Activity for runtime permission
    fun requestPermission(
        activity: PlatformActivity?,
        onFinished: (Boolean) -> Unit = {}
    )

    suspend fun areNotificationsEnabled(): Boolean

    // Request code for permission callback (Android)
    companion object {
        val REQUEST_CODE_NOTIFICATIONS: Int
    }
}
