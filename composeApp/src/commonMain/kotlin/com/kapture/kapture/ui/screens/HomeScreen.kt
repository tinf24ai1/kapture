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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
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
                    Icon(Icons.Rounded.Rotate90DegreesCw, "Spin")
                }
                FloatingActionButton(
                    onClick = {

                    },
                ) {
                    Icon(Icons.Rounded.Add, "Add")
                }
            }
        }
    }
}
