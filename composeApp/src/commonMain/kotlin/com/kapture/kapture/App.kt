package com.kapture.kapture

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kapture.kapture.storage.Item
import com.kapture.kapture.storage.LocalStorage
import com.kapture.kapture.storage.MinHeap
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar
import com.kapture.kapture.ui.components.NotificationsDeniedDialog
import androidx.compose.runtime.remember
import com.kapture.kapture.ai.AIService
import com.kapture.kapture.ai.AIViewModel
import com.kapture.kapture.ai.IdeaState
import com.kapture.kapture.logger.Logger

@Composable
@Preview
fun App(

    showPermissionHintDialog: Boolean = false,
    onDismissPermissionHint: () -> Unit = {},

) {
    val minHeap = remember {
        val heap = MinHeap()
        val storedItems: List<Item>? = LocalStorage.restore("MinHeap")
        storedItems?.forEach { heap.add(it) }
        heap
    }

    // test
    val aiService = AIService("")

    val viewModel = AIViewModel(aiService)

    val state by viewModel.uiState.collectAsState()

    viewModel.onGenerateClicked()

    when (state) {
        is IdeaState.Failure -> throw Exception((state as IdeaState.Failure).message)
        is IdeaState.Idle -> null
        is IdeaState.Loading -> null
        is IdeaState.Success -> {
            Logger.d("Response Title", (state as IdeaState.Success).title)
            Logger.d("Response Description", (state as IdeaState.Success).desc)
        }
    }

    MaterialTheme {
        NotificationsDeniedDialog(
            visible = showPermissionHintDialog,
            onDismiss = onDismissPermissionHint,
        )

        BottomNavigationBar(
            minHeap = minHeap,
        )
    }
}