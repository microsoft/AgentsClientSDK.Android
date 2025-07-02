package com.example.textclientapp.appUI

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microsoft.agentsclientsdk.AgentsClientSDK
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    isVoiceRecording: Boolean,
    onRecordClick: () -> Unit,
    messages: List<UiChatMessage>,
    recognizedText: String
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true
        ) {
            items(messages) { messageItem ->
                MessageBubble(message = messageItem)
            }
        }
        ChatInputField(
            onSendClick = { text ->
                if (text.isNotBlank()) {
                    scope.launch {
                        Log.e("onclick text", "" + text)
                        AgentsClientSDK.sdk?.sendMessage(text)
                    }
                }
                AgentsClientSDK.sdk?.stopSpeaking()
            },
            onRecordClick = onRecordClick,
            recognizedText = recognizedText,
            isVoiceRecording = isVoiceRecording
        )
    }
}