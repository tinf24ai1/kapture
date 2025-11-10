package com.kapture.kapture.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Rotate90DegreesCw
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.LocalStorage
import com.kapture.kapture.ui.components.InputDialog
import com.kapture.kapture.ui.components.AddIdeaForms
import com.kapture.kapture.storage.MinHeap
import com.kapture.kapture.ui.components.DisplayIdea
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.todayAt

@Composable
fun HomeScreen() {

    val minHeap = remember {
        val heap = MinHeap()
        val storedItems: List<Item>? = LocalStorage().restore("MinHeap")
        storedItems?.forEach { heap.add(it) }
        heap
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var releasedItemState by remember { mutableStateOf<Item?>(null) }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp
            ) {
                AddIdeaForms(
                    onSubmit = { title, releaseDate, idea ->
                        minHeap.add(Item(title, releaseDate, idea))
                        showDialog = false
                    },
                    onCancel = { showDialog = false }
                )
            }
        }
    }

    releasedItemState?.let { item ->
        Dialog(onDismissRequest = { releasedItemState = null }) {
            DisplayIdea(
                title = item.title,
                idea = item.idea,
                releaseDate = item.releaseDate,
                onClose = { releasedItemState = null }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        val today = kotlinx.datetime.Clock.System.todayAt(kotlinx.datetime.TimeZone.currentSystemDefault())
                        val topItem = minHeap.peek()
                        if (topItem != null && topItem.releaseDate <= today) {
                            releasedItemState = minHeap.poll()
                        }
                    },
                ) {
                    Icon(Icons.Rounded.Casino, contentDescription = "Lever")
                }

                FloatingActionButton(
                    onClick = {
                    },
                ) {
                    Icon(Icons.Rounded.Rotate90DegreesCw, Icons.Rounded.Rotate90DegreesCw::class.qualifiedName)
                }
                FloatingActionButton(
                    onClick = {
                        showDialog = !showDialog
                    },
                ) {
                    Icon(Icons.Rounded.Add, Icons.Rounded.Add::class.qualifiedName)
                }
            }
        }
    }
}