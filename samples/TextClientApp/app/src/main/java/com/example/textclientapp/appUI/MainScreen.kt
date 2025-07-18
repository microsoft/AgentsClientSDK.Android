package com.example.textclientapp.appUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.microsoft.agents.client.android.models.ChatMessage
import com.microsoft.agents.client.android.models.MessageResponse
import com.microsoft.agents.client.android.sdks.ClientSDK
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    isVoiceRecording: Boolean,
    onRecordClick: () -> Unit,
    agentsClientSdk: ClientSDK?
) {
    val showAgent = remember { mutableStateOf(false) }
    var messages by remember { mutableStateOf(listOf<UiChatMessage>()) }
    var recognizedText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val messageResponse by (agentsClientSdk?.liveData?.collectAsState()
        ?: remember { mutableStateOf(MessageResponse.Initial) })

    agentsClientSdk?.registerForContinuousListening(
        onRecognizing = { recognizedText = it },
        onRecognized = {
            recognizedText = it

            if (recognizedText.isNotBlank()) {
                val text = recognizedText
                recognizedText = ""
                scope.launch {
                    agentsClientSdk.sendMessage(text)
                }
            }
        }
    )

    LaunchedEffect(messageResponse) {
        when (messageResponse) {
            MessageResponse.Initial -> { /* do nothing */
            }

            is MessageResponse.Success -> {
                val messageData = (messageResponse as MessageResponse.Success<ChatMessage>).value
                messages = if (messageData.role == "bot") {
                    if (messageData.customView != null) {
                        listOf(
                            UiChatMessage(
                                id = "bot-${System.currentTimeMillis()}",
                                message = messageData.text,
                                isUser = false,
                                isAgentTyping = false,
                                messageData.customView!!
                            )
                        ) + messages.filterNot { it.isAgentTyping }
                    } else {
                        listOf(
                            UiChatMessage(
                                id = "bot-${System.currentTimeMillis()}",
                                message = messageData.text,
                                isUser = false,
                                isAgentTyping = false
                            )
                        ) + messages.filterNot { it.isAgentTyping }
                    }

                } else {
                    listOf(
                        UiChatMessage(
                            id = "user-${System.currentTimeMillis()}",
                            message = messageData.text,
                            isUser = true,
                            isAgentTyping = false
                        )
                    ) + messages.filterNot { it.isAgentTyping }
                }
            }

            is MessageResponse.Typing -> {
                if (messages.none { it.isAgentTyping }) {
                    messages = listOf(
                        UiChatMessage(
                            id = "bot-${System.currentTimeMillis()}",
                            message = "Typing...",
                            isUser = false,
                            isAgentTyping = true
                        )
                    ) + messages
                }
            }

            is MessageResponse.Failure -> {
                val messageData = (messageResponse as MessageResponse.Failure<ChatMessage>).value
                messages = listOf(
                    UiChatMessage(
                        id = "bot-${System.currentTimeMillis()}",
                        message = messageData.text,
                        isUser = false,
                        isAgentTyping = false
                    )
                ) + messages.filterNot { it.isAgentTyping }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        VanillaApp()
        if (showAgent.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showAgent.value = false }
            ) {
                ChatScreen(
                    modifier = Modifier
                        .fillMaxWidth(0.95f) // Adjust the width to 95% of the screen width
                        .fillMaxHeight(0.70f) // Adjust the height to 70% of the screen height
                        .align(Alignment.BottomEnd) // Align to the bottom end (right)
                        .padding(16.dp) // Add padding around the screen
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .border(
                            1.dp,
                            Color.Gray,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        ), // Add a border for better visibility
                    isVoiceRecording = isVoiceRecording,
                    onRecordClick = {
                        onRecordClick()
                        showAgent.value = false
                    },
                    messages = messages,
                    recognizedText = recognizedText,
                    agentsClientSdk = agentsClientSdk
                )
            }
        } else {
            PillUI(
                isVoiceRecording = isVoiceRecording,
                onKeyboardClick = { showAgent.value = true },
                onMicClick = {
                    onRecordClick()
                    showAgent.value = false
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 35.dp)
            )
        }
    }
}