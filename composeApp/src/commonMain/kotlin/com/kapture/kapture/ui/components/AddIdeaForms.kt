package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@Composable
fun AddIdeaForms(
    modifier: Modifier = Modifier,
    onSubmit: (title: String, releaseDate: LocalDate, idea: String, startDate: Int, endDate: Int) -> Unit = { _, _, _, _, _ -> },
    onCancel: () -> Unit = {},
    displayToastMessage: (String) -> Unit = {},
    releaseDate: (Int, Int) -> LocalDate
) {
    var title by remember { mutableStateOf("") }
    var idea by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableStateOf(1f..14f) }

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

        Text(
            text = "Capsule Trigger Range",
        )
        RangeSlider(
            value = sliderPosition,
            steps = 12,
            onValueChange = { range -> sliderPosition = range },
            valueRange = 1f..14f,
            colors = SliderDefaults.colors(),
        )
        Text(
            text = "Ready in ${
                if (sliderPosition.start.toInt() == sliderPosition.endInclusive.toInt())
                    "${sliderPosition.start.toInt()}"
                else
                    "${sliderPosition.start.toInt()} to ${sliderPosition.endInclusive.toInt()}"
            } Day${
                if (sliderPosition.endInclusive.toInt() != 1) "s" else ""
            }",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(0.dp)) // Just use Columns Arrangement.spacedBy

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    onSubmit(
                        title.trim(),
                        releaseDate(sliderPosition.start.toInt(), sliderPosition.endInclusive.toInt()),
                        idea.trim(),
                        sliderPosition.start.toInt(),
                        sliderPosition.endInclusive.toInt()
                    )
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
