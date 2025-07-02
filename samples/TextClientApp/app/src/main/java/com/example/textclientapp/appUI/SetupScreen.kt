package com.example.textclientapp.appUI

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(
    schemaName: String,
    environmentId: String,
    onSchemaNameChange: (String) -> Unit,
    onEnvIdChange: (String) -> Unit,
    enableSpeech: Boolean,
    onEnableSpeechChange: (Boolean) -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bot schema name", style = MaterialTheme.typography.titleMedium)
        BasicTextField(
            value = schemaName,
            onValueChange = onSchemaNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (schemaName.isEmpty()) {
                        Text(
                            text = "Optional",
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )
        Text("Environment Id", style = MaterialTheme.typography.titleMedium)
        BasicTextField(
            value = environmentId,
            onValueChange = onEnvIdChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (environmentId.isEmpty()) {
                        Text(
                            text = "Optional",
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(vertical = 8.dp)
//        ) {
//            androidx.compose.material3.Switch(
//                checked = enableSpeech,
//                onCheckedChange = onEnableSpeechChange
//            )
//            Text("Enable Speech Service", modifier = Modifier.padding(start = 8.dp))
//        }
        Button(
            onClick = { onProceed() },
            enabled = true,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Proceed")
        }
    }
}