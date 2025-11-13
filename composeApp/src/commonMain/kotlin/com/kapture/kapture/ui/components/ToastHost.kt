package com.kapture.kapture.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ToastHost(
    toastMessage: String?,
    clearToastMessage: () -> Unit,
    durationMillis: Long = 3000,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        if (!toastMessage.isNullOrEmpty()) {
            visible = true
            delay(durationMillis)
            visible = false
            delay(200)
            clearToastMessage()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = toastMessage ?: "",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
