package com.example.textclientapp.appUI

import android.view.View

data class UiChatMessage(
    val id: String,
    val message: String,
    val isUser: Boolean,
    val isAgentTyping: Boolean,
    val customView: View? = null
)