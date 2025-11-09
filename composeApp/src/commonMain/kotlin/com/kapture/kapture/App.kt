package com.kapture.kapture

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.kapture.kapture.ui.components.BottomNavigationBar

@Composable
@Preview
fun App(
    onRefreshFabClick: () -> Unit = {},
    onAddFabClick: () -> Unit = {},
) {
    MaterialTheme {
        BottomNavigationBar(
            onRefreshFabClick = onRefreshFabClick,
            onAddFabClick = onAddFabClick,
        )
    }
}