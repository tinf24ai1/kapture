package com.kapture.kapture.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Rotate90DegreesCw
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.reminder.createReminderScheduler
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.MinHeap
import com.kapture.kapture.ui.components.AddIdeaForms
import com.kapture.kapture.ui.components.DisplayIdea
import com.kapture.kapture.ui.components.ToastHost
import kapture.composeapp.generated.resources.Res
import kapture.composeapp.generated.resources.gumballmachine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

// Home Screen displaying the main interface with options to add and retrieve Ideas
@Composable
fun HomeScreen(minHeap : MinHeap, addToArchiveList: (Item) -> Unit, releaseDate: (Int, Int) -> LocalDate) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val clearToastMessage: () -> Unit = {
        toastMessage = null
    }
    val displayToastMessage: (String) -> Unit = { message ->
        toastMessage = message
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var releasedItemState by remember { mutableStateOf<Item?>(null) }
    fun changeReleasedItemState(state: Item?) {
        releasedItemState = state
    }

    val scheduler = remember { createReminderScheduler() }

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
                    onSubmit = { title, releaseDate, idea, startDate, endDate ->
                        Logger.d(
                            "Reminder",
                            "[AddIdea] new idea: '$title' - releaseDate=$releaseDate)"
                        )

                        val newItem = Item(title = title, releaseDate = releaseDate, idea = idea, startDate = startDate, endDate = endDate)
                        minHeap.add(newItem)

                        scheduler.schedule(newItem, hour = 10, minute = 0)

                        showDialog = false
                    },
                    displayToastMessage = displayToastMessage,
                    releaseDate = releaseDate,
                    onCancel = { showDialog = false }
                )
            }
        }
    }

    releasedItemState?.let { item ->
        Dialog(onDismissRequest = { }) {
            DisplayIdea(
                item = item,
                minHeap = minHeap,
                releaseDate = releaseDate,
                scheduler = scheduler,
                addToArchiveList = addToArchiveList,
                displayToastMessage = displayToastMessage,
                onClose = {
                    changeReleasedItemState(null)

                }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    )
    {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Kapture",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Box {
                Box(
                    modifier = Modifier.fillMaxSize().padding(36.dp,120.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.gumballmachine),
                            contentDescription = "Gumball Machine",
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
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
                            } else {
                                displayToastMessage("Currently no Idea ready!")
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
        ToastHost(toastMessage = toastMessage, clearToastMessage = clearToastMessage)
    }
}