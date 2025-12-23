package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kapture.kapture.logger.Logger
import com.kapture.kapture.reminder.ReminderScheduler
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.MinHeap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

@Composable
fun DisplayIdea(
    item: Item,
    minHeap: MinHeap,
    releaseDate: (Int, Int) -> LocalDate,
    addToArchiveList: (Item) -> Unit,
    modifier: Modifier = Modifier,
    displayToastMessage: (String) -> Unit,
    onClose: () -> Unit,
    scheduler: ReminderScheduler,

    ) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Idea is ready",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.idea,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = item.releaseDate.format(LocalDate.Format {
                                        dayOfMonth()
                                        chars(". ")
                                        monthName(MonthNames.ENGLISH_FULL)
                                        char(' ')
                                        year()
                                    }),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            border = AssistChipDefaults.assistChipBorder(false),
                            leadingIcon = { Icon(Icons.Rounded.CalendarMonth, Icons.Rounded.CalendarMonth::class.qualifiedName) },
                            shape = RoundedCornerShape(16.dp),
                            elevation = AssistChipDefaults.assistChipElevation(4.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = {
                        val i = Item(
                            title = item.title,
                            releaseDate = releaseDate(item.startDate, item.endDate),
                            idea = item.idea,
                            startDate = item.startDate,
                            endDate = item.endDate
                        )
                        minHeap.add(i)
                        scheduler.schedule(i, hour = 10, minute = 0)
                        Logger.i("Item", "Item '${i.title}' got new timestamp assigned: ${i.releaseDate}")
                        displayToastMessage("Idea is back in Capsule")
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Refresh, Icons.Rounded.Refresh::class.qualifiedName)
                }

                Button(
                    onClick = {
                        addToArchiveList(item)
                        Logger.i("Item", "Item '${item.title}' got moved to archive")
                        displayToastMessage("Idea saved to Archive")
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Archive, Icons.Rounded.Archive::class.qualifiedName)
                }

                Button(
                    onClick = {
                        displayToastMessage("Idea removed")
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Delete, Icons.Rounded.Delete::class.qualifiedName)
                }
            }
        }
    }
}