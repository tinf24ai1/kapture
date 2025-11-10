package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlin.random.Random

@Composable
fun AddIdeaForms(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, releaseDate: LocalDate, idea: String) -> Unit = { _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    val baseDate = Clock.System.todayAt(TimeZone.currentSystemDefault())
    var title by remember { mutableStateOf("") }
    var idea by remember { mutableStateOf("") }

    val releaseDate = remember {
        val start = baseDate.plus(1, DateTimeUnit.DAY).toEpochDays()
        val end = baseDate.plus(8, DateTimeUnit.DAY).toEpochDays()
        val randomDay = Random.nextInt(start, end + 1)
        LocalDate.fromEpochDays(randomDay)
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add a New Idea", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = idea,
            onValueChange = { idea = it },
            label = { Text("Your Idea") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    onSubmit(title, releaseDate, idea)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Submit")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}
