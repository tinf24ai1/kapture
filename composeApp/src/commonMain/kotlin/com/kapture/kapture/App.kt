package com.kapture.kapture

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar
import com.kapture.kapture.ui.components.NotificationsDeniedDialog


@Composable
@Preview
fun App(

    showPermissionHintDialog: Boolean = false,
    onDismissPermissionHint: () -> Unit = {},

) {
    MaterialTheme {
        NotificationsDeniedDialog(
            visible = showPermissionHintDialog,
            onDismiss = onDismissPermissionHint
        )


        BottomNavigationBar()
    }
}