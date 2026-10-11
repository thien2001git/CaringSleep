package com.hoang.auth.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenProvider()

        return if (!token.isNullOrEmpty()) {
            val authenticatedRequest =
                originalRequest
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
