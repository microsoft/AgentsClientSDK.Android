package com.example.textclientapp.appUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.textclientapp.R
import com.microsoft.agents.client.android.AgentsClientSDK

@Composable
fun ChatInputField(
    modifier: Modifier = Modifier,
    onSendClick: (String) -> Unit,
    onRecordClick: () -> Unit,
    recognizedText: String,
    isVoiceRecording: Boolean
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(recognizedText) {
        textState = TextFieldValue(recognizedText)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp, bottom = 8.dp, top = 8.dp)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                textStyle = MaterialTheme.typography.bodyMedium,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 8.dp, top = 8.dp),
                    ) {
                        if (textState.text.isEmpty()) {
                            Text(
                                text = "Type a message...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )
            IconButton(
                onClick = {
                    onSendClick(textState.text)
                    textState = TextFieldValue("")
                },
                modifier = Modifier
                    .padding(0.dp)
                    .size(36.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
            if (AgentsClientSDK.isSpeechEnabled()) {
                IconButton(
                    onClick = { onRecordClick() },
                    modifier = Modifier
                        .padding(0.dp)
                        .size(36.dp)
                ) {
                    if (isVoiceRecording) {
                        Image(
                            painter = painterResource(id = R.drawable.mic_24px),
                            contentDescription = "Stop record",
                            colorFilter = ColorFilter.tint(Color.Red)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.mic_24px),
                            contentDescription = "Start Record",
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
        }
    }
}