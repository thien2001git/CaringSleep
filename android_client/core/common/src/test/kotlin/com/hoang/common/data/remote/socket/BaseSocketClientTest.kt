package com.hoang.common.data.remote.socket

import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class BaseSocketClientTest {
    private val socketClient = object : BaseSocketClient(OkHttpClient()) {}

    @Test
    fun `events flow is initialized`() {
        assertNotNull(socketClient.events)
    }

    @Test
    fun `send fails when not connected`() {
        assertFalse(socketClient.send("test"))
        assertFalse(socketClient.send(byteArrayOf(1, 2, 3)))
    }

    @Test
    fun `disconnect returns false when not connected`() {
        assertFalse(socketClient.disconnect())
    }
}
