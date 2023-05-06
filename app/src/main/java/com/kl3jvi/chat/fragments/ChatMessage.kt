package com.kl3jvi.chat.fragments

sealed class ChatMessage {
    data class SentMessage(val content: String) : ChatMessage()
    data class ReceivedMessage(val content: String) : ChatMessage()
}
