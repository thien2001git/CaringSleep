package com.hoang.common.data.remote.socket

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

abstract class BaseSocketClient(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) {
    sealed class SocketEvent {
        data class Connected(
            val response: Response,
        ) : SocketEvent()

        data class TextMessage(
            val text: String,
        ) : SocketEvent()

        data class BinaryMessage(
            val bytes: ByteArray,
        ) : SocketEvent() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as BinaryMessage
                return bytes.contentEquals(other.bytes)
            }

            override fun hashCode(): Int = bytes.contentHashCode()
        }

        data class Closing(
            val code: Int,
            val reason: String,
        ) : SocketEvent()

        data class Closed(
            val code: Int,
            val reason: String,
        ) : SocketEvent()

        data class Failure(
            val throwable: Throwable,
            val response: Response?,
        ) : SocketEvent()
    }

    private var webSocket: WebSocket? = null

    private val _events =
        MutableSharedFlow<SocketEvent>(
            replay = 0,
            extraBufferCapacity = 64,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val events: SharedFlow<SocketEvent> = _events.asSharedFlow()

    fun connect(
        url: String,
        headers: Map<String, String> = emptyMap(),
    ) {
        val requestBuilder = Request.Builder().url(url)
        headers.forEach { (key, value) ->
            requestBuilder.header(key, value)
        }

        webSocket =
            okHttpClient.newWebSocket(
                requestBuilder.build(),
                object : WebSocketListener() {
                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response,
                    ) {
                        _events.tryEmit(SocketEvent.Connected(response))
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        text: String,
                    ) {
                        _events.tryEmit(SocketEvent.TextMessage(text))
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        bytes: ByteString,
                    ) {
                        _events.tryEmit(SocketEvent.BinaryMessage(bytes.toByteArray()))
                    }

                    override fun onClosing(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        _events.tryEmit(SocketEvent.Closing(code, reason))
                    }

                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        _events.tryEmit(SocketEvent.Closed(code, reason))
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?,
                    ) {
                        _events.tryEmit(SocketEvent.Failure(t, response))
                    }
                },
            )
    }

    fun send(text: String): Boolean = webSocket?.send(text) ?: false

    fun send(bytes: ByteArray): Boolean = webSocket?.send(bytes.toByteString()) ?: false

    fun disconnect(
        code: Int = 1000,
        reason: String = "Normal Closure",
    ): Boolean {
        val result = webSocket?.close(code, reason) ?: false
        webSocket = null
        return result
    }
}
