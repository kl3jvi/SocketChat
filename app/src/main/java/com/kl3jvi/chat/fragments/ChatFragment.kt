package com.kl3jvi.chat.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.kl3jvi.chat.R
import com.kl3jvi.chat.databinding.FragmentChatBinding
import com.kl3jvi.chat.recipientMessage
import com.kl3jvi.chat.senderMessage
import com.kl3jvi.chat.util.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class ChatFragment : Fragment(R.layout.fragment_chat), KoinComponent {

    private val viewModel: ChatViewModel by viewModel()
    private var binding: FragmentChatBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        setupSender()

        collect(viewModel.messages) { messages ->
            val a = messages.map {
                when (it) {
                    is ChatMessage.ReceivedMessage -> "REceived ${it.content}"
                    is ChatMessage.SentMessage -> "sent ${it.content}"
                }
            }
            Log.e("Ckemi", a.toString())
            binding?.messageRv?.withModels {
                messages.forEach { type ->
                    when (type) {
                        is ChatMessage.ReceivedMessage -> recipientMessage {
                            id(type.content.length)
                            message(type.content)
                        }

                        is ChatMessage.SentMessage -> senderMessage {
                            id(type.content.length)
                            message(type.content)
                        }
                    }
                }
            }
        }
    }

    private fun setupSender() {
        binding?.sendMessageButton?.setOnClickListener {
            val message = binding?.messageInput?.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding?.messageInput?.text?.clear()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
