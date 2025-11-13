package com.kapture.kapture

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.LocalStorage
import com.kapture.kapture.storage.MinHeap
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar
import com.kapture.kapture.ui.components.NotificationsDeniedDialog
import com.kapture.kapture.reminder.createReminderScheduler
import com.kapture.kapture.reminder.ReminderScheduler
import androidx.compose.runtime.remember
import com.kapture.kapture.reminder.scheduleNextWithLog

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

    val scheduler: ReminderScheduler = remember { createReminderScheduler() }

    val sorted = remember(minHeap.items) { minHeap.items.sortedBy { it.releaseDate } }
        LaunchedEffect(minHeap.items) {
            //Pick earliest idea and schedule notification for 10:00 AM on its release date
            scheduler.scheduleNextWithLog(
                source = "AppStart",
                itemsSortedByDate = minHeap.items.sortedBy { it.releaseDate },
                hour = 10, minute = 0
            )
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