package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@Composable
fun AddIdeaForms(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, releaseDate: LocalDate, idea: String) -> Unit = { _, _, _ -> },
    onCancel: () -> Unit = {},
    displayToastMessage: (String) -> Unit = {},
    releaseDate: () -> LocalDate
) {
    var title by remember { mutableStateOf("") }
    var idea by remember { mutableStateOf("") }

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
            label = { Text("Idea") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = idea,
            onValueChange = { idea = it },
            label = { Text("Description") },
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
                    onSubmit(title.trim(), releaseDate(), idea.trim())
                    displayToastMessage("Added Idea")
                },
                enabled = (title.trim() != "" && idea.trim() != ""),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Check, Icons.Rounded.Check::class.qualifiedName)
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Cancel, Icons.Rounded.Cancel::class.qualifiedName)
            }
        }
    }
}
