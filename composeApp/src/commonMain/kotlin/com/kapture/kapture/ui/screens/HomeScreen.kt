package com.kapture.kapture.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Rotate90DegreesCw
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kapture.kapture.notifications.*
import androidx.compose.ui.window.Dialog
import com.kapture.kapture.storage.MinHeap
import com.kapture.kapture.storage.Item
import com.kapture.kapture.ui.components.AddIdeaForms
import com.kapture.kapture.ui.components.DisplayIdea
import kapture.composeapp.generated.resources.Res
import kapture.composeapp.generated.resources.gumballmachine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.ui.components.ToastHost
import kotlinx.datetime.toLocalDateTime
import com.kapture.kapture.reminder.createReminderScheduler

@Composable
fun HomeScreen(minHeap : MinHeap, addToArchiveList: (Item) -> Unit, releaseDate: () -> LocalDate) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val clearToastMessage: () -> Unit = {
        toastMessage = null
    }
    val displayToastMessage: (String) -> Unit = { message ->
        toastMessage = message
    }

    val notificationService = remember { NotificationService() }
    val notificationVm = remember { AppViewModel(notificationService) }

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
                    onSubmit = { title, releaseDate, idea ->
                        minHeap.add(Item(title, releaseDate, idea))
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
        Dialog(onDismissRequest = { changeReleasedItemState(null) }) {
            DisplayIdea(
                item = item,
                minHeap = minHeap,
                releaseDate = releaseDate,
                addToArchiveList = addToArchiveList,
                onClose = { changeReleasedItemState(null) }
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
                    /*TEST BUTTON FOR SCHEDULING NOTIFICATIONS
                    FloatingActionButton(
                        onClick = {
                            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            // Fake-item for Test
                            val testItem = Item(
                                title = "TEST-NOTIF-${now.hour}-${now.minute}",
                                releaseDate = now.date,
                                idea = "debug"
                            )
                            // schedule in ~ 1 min
                            val testHour = now.hour
                            val testMinute = (now.minute + 1) % 60

                            val hm = "${testHour.toString().padStart(2,'0')}:${testMinute.toString().padStart(2,'0')}"

                            Logger.d("Reminder", "[Debug] plan Test-Notification for ${now.date} at $hm")
                            scheduler.schedule(testItem, hour = testHour, minute = testMinute)
                        },
                    ) {
                        Icon(Icons.Rounded.Rotate90DegreesCw, Icons.Rounded.Rotate90DegreesCw::class.qualifiedName)
                    }
                    */
                }
            }
        }
        ToastHost(toastMessage = toastMessage, clearToastMessage = clearToastMessage)
    }
}