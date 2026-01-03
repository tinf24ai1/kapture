package com.kapture.kapture.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import kapture.composeApp.MR

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
        title = { Text(stringResource(MR.strings.notification_denied_title)) },
        text = {
            Text(stringResource(MR.strings.notification_denied_text))
        }
    )
}
