package com.kapture.kapture.notifications

// Vertrag (wie im KMP-Bits-Guide): gleiche Signaturen auf beiden Plattformen
expect class NotificationService() {

    fun showNotification(
        title: String,
        message: String? = null
    )

    fun requestPermission(
        activity: PlatformActivity?,           // Android braucht Activity, iOS ignoriert
        onFinished: (Boolean) -> Unit = {}     // Callback für "granted?" (einfach gehalten)
    )

    suspend fun areNotificationsEnabled(): Boolean

    companion object {
        val REQUEST_CODE_NOTIFICATIONS: Int    // Android: für onRequestPermissionsResult
    }
}
