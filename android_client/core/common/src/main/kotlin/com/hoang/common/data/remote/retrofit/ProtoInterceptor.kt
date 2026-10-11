package com.hoang.common.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class ProtoInterceptor(
    private val preferProto: Boolean = true,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (preferProto && originalRequest.header("Accept") == null) {
            requestBuilder.header("Accept", CONTENT_TYPE_PROTOBUF)
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val CONTENT_TYPE_PROTOBUF = "application/x-protobuf"
    }
}
