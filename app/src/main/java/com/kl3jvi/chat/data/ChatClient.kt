package com.kl3jvi.chat.data

import android.util.Log
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress

class ChatClient {
    private var socket: Socket? = null
    private var input: ByteReadChannel? = null
    private var output: ByteWriteChannel? = null

    suspend fun connect(serverIP: String, port: Int, username: String) {
        Log.d("ChatClient", "Attempting to connect to server at $serverIP:$port")
        val serverAddress = InetSocketAddress(serverIP, port)
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(serverAddress)
        input = socket?.openReadChannel()
        output = socket?.openWriteChannel(autoFlush = true)

        Log.d("ChatClient", "Connected to server, sending username: $username")
        val byteArray = username.toByteArray(Charsets.UTF_8)
        output?.writeFully(byteArray, 0, byteArray.size)
        output?.flush()
        Log.d("ChatClient", "Username sent")
    }

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            val byteArray = message.toByteArray(Charsets.UTF_8)
            output?.writeFully(byteArray, 0, byteArray.size)
            output?.flush()
        }
    }

    suspend fun receiveMessage(): String? {
        Log.d("ChatClient", "Before receiving message")
        val message = withContext(Dispatchers.IO) {
            input?.readUTF8Line()
        }
        Log.d("ChatClient", "After receiving message: $message")
        return message
    }
    fun disconnect() {
        socket?.close()
    }
}
