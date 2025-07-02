package com.example.textclientapp.appUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

@Composable
fun MessageBubble(message: UiChatMessage) {
    val backgroundColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFE5E5E5)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .wrapContentWidth(alignment)
    ) {
        if (message.isAgentTyping) {
            TypingIndicator()
        } else if (message.customView != null) {
            AndroidView(
                factory = { message.customView },
                modifier = Modifier
                    .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
            )
        } else {
            Text(
                text = message.message,
                modifier = Modifier
                    .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val animationDuration = 300
    val animationOffsets = remember { mutableStateListOf(0f, 0f, 0f) }
    LaunchedEffect(Unit) {
        while (true) {
            animationOffsets[0] = if (animationOffsets[0] == 0f) 8f else 0f
            delay(animationDuration.toLong())
            animationOffsets[1] = if (animationOffsets[1] == 0f) 8f else 0f
            delay((animationDuration * 0.15).toLong())
            animationOffsets[2] = if (animationOffsets[2] == 0f) 8f else 0f
            delay((animationDuration * 0.3).toLong())
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(animationOffsets[0])
        Dot(animationOffsets[1])
        Dot(animationOffsets[2])
    }
}

@Composable
fun Dot(offset: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .offset(y = offset.dp)
            .clip(CircleShape)
            .background(Color(0xFFE5E5E5))
    )
}