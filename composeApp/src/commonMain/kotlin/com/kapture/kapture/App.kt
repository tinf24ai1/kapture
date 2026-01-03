package com.kapture.kapture

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.kapture.kapture.storage.ItemModel
import com.kapture.kapture.storage.ItemList
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar
import com.kapture.kapture.ui.components.NotificationsDeniedDialog
import androidx.compose.runtime.remember
import com.kapture.kapture.ai.AIService
import com.kapture.kapture.ai.AIViewModel
import com.kapture.kapture.storage.LocalItemRepository

// Google Gemini API Key for AI services
const val API_KEY = "YOUR_API_KEY_GOES_HERE"

@Composable
@Preview
fun App(

    showPermissionHintDialog: Boolean = false,
    onDismissPermissionHint: () -> Unit = {},

) {
    val itemList = remember {
        val list = ItemList()
        val storedItemModels: List<ItemModel> = LocalItemRepository.load()
        storedItemModels.forEach { list.add(it) }
        list
    }

    val aiService = AIService(API_KEY)

    val aiViewModel = AIViewModel(aiService)

    MaterialTheme {
        NotificationsDeniedDialog(
            visible = showPermissionHintDialog,
            onDismiss = onDismissPermissionHint,
        )

        BottomNavigationBar(
            itemList = itemList,
            aiViewModel = aiViewModel
        )
    }
}