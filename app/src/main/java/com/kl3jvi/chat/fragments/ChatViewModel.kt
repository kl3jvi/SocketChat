package com.kl3jvi.chat.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kl3jvi.chat.data.ChatClient
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatClient: ChatClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun connect(serverIp: String, port: Int, username: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineName("Connector Coroutine")) {
            chatClient.connect(serverIp, port, username)
//            192.168.1.7
//            10.0.2.2
            startReceivingMessages()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatClient.sendMessage(message)
            val sentMessage = ChatMessage.SentMessage(message)
            _messages.update { messages -> messages + sentMessage }
        }
    }

    private fun startReceivingMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val message = chatClient.receiveMessage()
                if (message == null) {
                    // Handle disconnection or errors
                    break
                } else {
                    val receivedMessage = ChatMessage.ReceivedMessage(message)
                    _messages.update { messages -> messages + receivedMessage }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatClient.disconnect()
    }
}
