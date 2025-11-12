package com.kapture.kapture.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// Simple reusable dialog shown when notifications were denied.

@Composable
fun NotificationsDeniedDialog(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = { Text("Notifications disabled") },
        text = {
            Text(
                "In order to use the time capsule feature to its fullest, you need to allow notifications. " +
                        "If you change your mind, you can enable notifications in the app settings."
            )
        }
    )
}
