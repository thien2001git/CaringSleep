package com.hoang.common.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import java.util.concurrent.TimeUnit

object BaseRetrofitClient {
    fun createOkHttpClient(
        interceptors: List<Interceptor> = emptyList(),
        connectTimeoutSeconds: Long = 30,
        readTimeoutSeconds: Long = 30,
        enableProtoInterceptor: Boolean = true,
    ): OkHttpClient {
        val builder =
            OkHttpClient
                .Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)

        if (enableProtoInterceptor) {
            builder.addInterceptor(ProtoInterceptor())
        }

        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        builder.addInterceptor(loggingInterceptor)

        interceptors.forEach { builder.addInterceptor(it) }

        return builder.build()
    }

    fun createRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient = createOkHttpClient(),
        useProtoFirst: Boolean = true,
    ): Retrofit {
        val builder =
            Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)

        if (useProtoFirst) {
            builder.addConverterFactory(ProtoConverterFactory.create())
            builder.addConverterFactory(GsonConverterFactory.create())
        } else {
            builder.addConverterFactory(GsonConverterFactory.create())
            builder.addConverterFactory(ProtoConverterFactory.create())
        }

        return builder.build()
    }

    inline fun <reified T> createService(
        baseUrl: String,
        okHttpClient: OkHttpClient = createOkHttpClient(),
        useProtoFirst: Boolean = true,
    ): T = createRetrofit(baseUrl, okHttpClient, useProtoFirst).create(T::class.java)
}
