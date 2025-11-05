package com.kapture.kapture.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Rotate90DegreesCw
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kapture.kapture.ui.components.InputDialog
import com.kapture.kapture.storage.MinHeap
import com.kapture.kapture.storage.Item
import kotlinx.datetime.Instant

@Composable
fun HomeScreen() {

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        InputDialog(
            onConfirm = { },
            onDismiss = {
                showDialog = !showDialog
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                    },
                ) {
                    Icon(Icons.Rounded.Rotate90DegreesCw, Icons.Rounded.Rotate90DegreesCw::class.qualifiedName)
                }
                FloatingActionButton(
                    onClick = {
                        showDialog = !showDialog
                    },
                ) {
                    Icon(Icons.Rounded.Add, Icons.Rounded.Add::class.qualifiedName)
                }
            }
        }
    }
}