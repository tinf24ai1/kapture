package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun InputDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).width(280.dp)
                ) {
                    Text("Input-Dialog")
                    Spacer(modifier = Modifier.height(60.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                onConfirm()
                                onDismiss()
                            }
                        ) {
                            Icon(Icons.Rounded.Check, Icons.Rounded.Check::class.qualifiedName)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    )
}