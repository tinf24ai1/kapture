package com.kapture.kapture.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kapture.kapture.ai.AIViewModel
import com.kapture.kapture.ai.IdeaState
import com.kapture.kapture.logger.Logger
import dev.icerock.moko.resources.compose.stringResource
import kapture.composeApp.MR
import kotlinx.datetime.*

// Form component to add a new Idea with title, description and trigger range
@Composable
fun AddIdeaForms(
    onSubmit: (title: String, releaseDate: LocalDate, idea: String, startDate: Int, endDate: Int) -> Unit = { _, _, _, _, _ -> },
    onCancel: () -> Unit = {},
    displayToastMessage: (String) -> Unit = {},
    releaseDate: (Int, Int) -> LocalDate,
    aiViewModel: AIViewModel,
    state: IdeaState
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableStateOf(1f..14f) }

    var isLoading by remember { mutableStateOf(false) }
    val setIsLoading = { b: Boolean -> isLoading = b }

    var aiErrorMessage by remember { mutableStateOf("") }
    val setAiErrorMessage = { str: String -> aiErrorMessage = str }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(MR.strings.add_idea_title), style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(MR.strings.add_idea_title_label)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Handle the different AI idea generation states
        LaunchedEffect(state) {
            when (state) {
                is IdeaState.Failure -> {
                    setIsLoading(false)
                    setAiErrorMessage(state.message)

                    Logger.e("IdeaState.Failure", aiErrorMessage)
                }
                is IdeaState.Idle -> {
                }
                is IdeaState.Loading -> {
                    setIsLoading(true)
                    setAiErrorMessage("")
                }
                is IdeaState.Success -> {
                    setIsLoading(false)
                    setAiErrorMessage("")

                    title = state.title
                    desc = state.desc

                    Logger.d("Response Title", state.title)
                    Logger.d("Response Description", state.desc)
                }
            }
        }

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text(stringResource(MR.strings.add_idea_desc_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        // AI Assistant
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Button(
                onClick = {
                    aiViewModel.onGenerateClicked(title, desc)
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                AnimatedContent(targetState = isLoading) { loading ->
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Rounded.SmartToy,
                            Icons.Rounded.SmartToy::class.qualifiedName,
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(3f).height(56.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(MR.strings.add_idea_google_gemini),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = Icons.Rounded.Info::class.qualifiedName,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // Display AI error message to user if any
        if (aiErrorMessage.isNotEmpty()) {
            Surface(
                modifier = Modifier.height(38.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(MR.strings.add_idea_google_gemini_failure),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = Icons.Rounded.Error::class.qualifiedName,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Text(
            text = stringResource(MR.strings.add_idea_trigger_range_label)
        )
        Surface(
            modifier = Modifier.height(115.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                val start = sliderPosition.start.toInt()
                val end = sliderPosition.endInclusive.toInt()

                val dayLabel = stringResource(MR.plurals.days, end, end)

                val addIdeaReadyString: String = if (start == end) {
                    stringResource(MR.strings.add_idea_ready_in_single, start, dayLabel)
                } else {
                    stringResource(MR.strings.add_idea_ready_in_range, start, end, dayLabel)
                }

                RangeSlider(
                    value = sliderPosition,
                    steps = 12,
                    onValueChange = { range -> sliderPosition = range },
                    valueRange = 1f..14f,
                    colors = SliderDefaults.colors(),
                )
                Text(
                    text = addIdeaReadyString,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(modifier = Modifier.height(0.dp)) // Just use Columns Arrangement.spacedBy

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val toastAddedIdea: String = stringResource(MR.strings.toast_added_idea)

            Button(
                onClick = {
                    onSubmit(
                        title.trim(),
                        releaseDate(sliderPosition.start.toInt(), sliderPosition.endInclusive.toInt()),
                        desc.trim(),
                        sliderPosition.start.toInt(),
                        sliderPosition.endInclusive.toInt()
                    )
                    displayToastMessage(toastAddedIdea)
                },
                enabled = (title.trim() != "" && desc.trim() != "" && state != IdeaState.Loading),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Check, Icons.Rounded.Check::class.qualifiedName)
            }

            OutlinedButton(
                onClick = {
                    setAiErrorMessage("")
                    onCancel()
                },
                enabled = (state != IdeaState.Loading),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Cancel, Icons.Rounded.Cancel::class.qualifiedName)
            }
        }
    }
}
