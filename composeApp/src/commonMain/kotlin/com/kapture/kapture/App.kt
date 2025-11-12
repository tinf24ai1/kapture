package com.kapture.kapture

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.LocalStorage
import com.kapture.kapture.storage.MinHeap
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar
import com.kapture.kapture.ui.components.NotificationsDeniedDialog

@Composable
@Preview
fun App(

    showPermissionHintDialog: Boolean = false,
    onDismissPermissionHint: () -> Unit = {},

) {
    val minHeap = remember {
        val heap = MinHeap()
        val storedItems: List<Item>? = LocalStorage.restore("MinHeap")
        storedItems?.forEach { heap.add(it) }
        heap
    }

    MaterialTheme {
        NotificationsDeniedDialog(
            visible = showPermissionHintDialog,
            onDismiss = onDismissPermissionHint
        )


        BottomNavigationBar(
            minHeap
        )
    }
}