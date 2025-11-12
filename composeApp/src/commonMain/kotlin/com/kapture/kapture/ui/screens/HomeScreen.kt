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
import com.kapture.kapture.ui.components.AddIdeaForms
import com.kapture.kapture.storage.MinHeap
import com.kapture.kapture.ui.components.DisplayIdea
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun HomeScreen(minHeap : MinHeap) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    var releasedItemState by remember { mutableStateOf<Item?>(null) }
    fun changeReleasedItemState(state: Item?) {
        releasedItemState = state
    }

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
        Dialog(onDismissRequest = { changeReleasedItemState(null) }) {
            DisplayIdea(
                title = item.title,
                idea = item.idea,
                releaseDate = item.releaseDate,
                onClose = { changeReleasedItemState(null) }
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
                        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                        val topItem = minHeap.peek()
                        if (topItem != null && topItem.releaseDate <= today) {
                            changeReleasedItemState(minHeap.poll())
                        }
                    },
                ) {
                    Icon(Icons.Rounded.Casino, Icons.Rounded.Rotate90DegreesCw::class.qualifiedName)
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