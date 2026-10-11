package com.hoang.common.data.remote.retrofit

import com.google.protobuf.MessageLite
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.converter.protobuf.ProtoConverterFactory

object ProtoResponseConverter {
    val factory: ProtoConverterFactory = ProtoConverterFactory.create()
    val protobufMediaType = "application/x-protobuf".toMediaType()

    fun <T : MessageLite> toByteArray(message: T): ByteArray = message.toByteArray()

    fun <T : MessageLite> T.toRequestBody(): RequestBody = toByteArray().toRequestBody(protobufMediaType)

    fun <T : MessageLite> ResponseBody.toProto(parser: (ByteArray) -> T): T = parser(bytes())
}
