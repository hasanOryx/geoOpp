package com.oryx.geoop

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class SocketClient {
    val uri = URI("ws://10.0.2.2:8080/ws")

    val message = MutableLiveData<String>()

    fun connectWebSocket(context: Context) {

        mWebSocketClient = object : WebSocketClient(uri) {
            override fun onError(ex: Exception?) {

            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {

            }

            override fun onOpen(serverHandshake: ServerHandshake) {

            }

            override fun onMessage(s: String) {
                message.postValue(s)
            }
        }
        mWebSocketClient.connect()
    }
}