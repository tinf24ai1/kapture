package com.kapture.kapture.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kapture.kapture.ai.AIViewModel
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.reminder.createReminderScheduler
import com.kapture.kapture.storage.ItemModel
import com.kapture.kapture.storage.ItemList
import com.kapture.kapture.ui.components.AddIdeaForms
import com.kapture.kapture.ui.components.DisplayIdea
import com.kapture.kapture.ui.components.ToastHost
import dev.icerock.moko.resources.compose.stringResource
import kapture.composeApp.MR
import kapture.composeapp.generated.resources.Res
import kapture.composeapp.generated.resources.gumballmachine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

// Home Screen displaying the main interface with options to add and retrieve Ideas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(itemList : ItemList, addToArchiveList: (ItemModel) -> Unit, releaseDate: (Int, Int) -> LocalDate, aiViewModel: AIViewModel) {
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val clearToastMessage: () -> Unit = {
        toastMessage = null
    }
    val displayToastMessage: (String) -> Unit = { message ->
        toastMessage = message
    }

    var showSheet by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    var releasedItemModelState by remember { mutableStateOf<ItemModel?>(null) }
    fun changeReleasedItemState(state: ItemModel?) {
        releasedItemModelState = state
    }

    val scheduler = remember { createReminderScheduler() }

    // State for AI Assistant (IdeaState)
    val state by aiViewModel.uiState.collectAsStateWithLifecycle()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                aiViewModel.resetState()
                showSheet = false
            },
            sheetState = sheetState
        ) {

            AddIdeaForms(
                onSubmit = { title, releaseDate, idea, startDate, endDate ->
                    Logger.d(
                        "Reminder",
                        "[AddIdea] new idea: '$title' - releaseDate=$releaseDate)"
                    )

                    val newItem = ItemModel(title = title, releaseDate = releaseDate, idea = idea, startDate = startDate, endDate = endDate)
                    itemList.add(newItem)

                    scheduler.schedule(newItem, hour = 10, minute = 0)

                    aiViewModel.resetState()
                    showSheet = false
                },
                displayToastMessage = displayToastMessage,
                releaseDate = releaseDate,
                onCancel = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            aiViewModel.resetState()
                            showSheet = false
                        }
                    }
                },
                aiViewModel = aiViewModel,
                state = state
            )
        }
    }

    releasedItemModelState?.let { item ->
        Dialog(onDismissRequest = { }) {
            DisplayIdea(
                itemModel = item,
                itemList = itemList,
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
                text = stringResource(MR.strings.home_title),
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
                            contentDescription = Res.drawable.gumballmachine::class.qualifiedName,
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    val toastNoIdeaReady: String = stringResource(MR.strings.toast_no_idea_ready)

                    FloatingActionButton(
                        onClick = {
                            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                            val topItem = itemList.peek()
                            if (topItem != null && topItem.releaseDate <= today) {
                                changeReleasedItemState(itemList.poll())
                            } else {
                                displayToastMessage(toastNoIdeaReady)
                            }
                        },
                    ) {
                        Icon(Icons.Rounded.Casino, Icons.Rounded.Casino::class.qualifiedName)
                    }
                    FloatingActionButton(
                        onClick = {
                            showSheet = !showSheet
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