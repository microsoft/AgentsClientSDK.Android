package com.example.textclientapp.appUI

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.textclientapp.R
import com.microsoft.agentsclientsdk.AgentsClientSDK

@Composable
fun PillUI(
    isVoiceRecording: Boolean,
    onKeyboardClick: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val micColor by animateColorAsState(
        targetValue = if (isVoiceRecording) Color.Red else Color(0xFF01678C),
        animationSpec = tween(durationMillis = 300)
    )

    val micSize by animateDpAsState(
        targetValue = if (isVoiceRecording) 54.dp else 54.dp,
        animationSpec = tween(durationMillis = 300)
    )

    val outlineWidth by animateDpAsState(
        targetValue = if (isVoiceRecording) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000)
    )

    Row(
        modifier = modifier
            .width(if (AgentsClientSDK.isSpeechEnabled()) 165.dp else 90.dp)
            .height(60.dp)
            .background(Color(0xDCFFFFFF), shape = RoundedCornerShape(26.dp))
            .border(outlineWidth, Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(26.dp))
            .padding(horizontal = 10.dp)
            .boxShadow(isVoiceRecording)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onKeyboardClick) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_24),
                contentDescription = "Keyboard",
                tint = Color(0xFF242424),
                modifier = Modifier.size(36.dp)
            )
        }

        if (AgentsClientSDK.isSpeechEnabled()) {
            IconButton(onClick = onMicClick) {
                Image(
                    painter = painterResource(id = R.drawable.mic_24px),
                    contentDescription = if (isVoiceRecording) "Stop Recording" else "Start Recording",
                    colorFilter = ColorFilter.tint(micColor),
                    modifier = Modifier
                        .size(micSize)
                        .background(Color(0xFF95B8C3), shape = CircleShape)
                        .border(1.dp, Color.White, shape = CircleShape)
                        .innerShadow(isVoiceRecording)
                )
            }
        }
    }
}

@Composable
fun Modifier.boxShadow(isVoiceRecording: Boolean): Modifier {
    return if (isVoiceRecording) {
        this.shadow(20.dp, CircleShape, clip = false)
    } else {
        this
    }
}

@Composable
fun Modifier.innerShadow(isVoiceRecording: Boolean): Modifier {
    return if (isVoiceRecording) {
        this.graphicsLayer {
            shadowElevation = 20.dp.toPx()
            shape = CircleShape
            clip = true
        }
    } else {
        this
    }
}