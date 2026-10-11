package com.hoang.auth.data.remote.retrofit

import com.hoang.common.data.remote.retrofit.BaseProtoCrudApiService
import com.hoang.moneycare.core.auth.presentation.proto.ProfileProto
import com.hoang.moneycare.core.auth.presentation.proto.ProfileResponseProto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileProtoApiService : BaseProtoCrudApiService<ProfileProto> {
    @GET("email/{email}")
    suspend fun findByEmail(
        @Path("email") email: String,
    ): Response<ProfileResponseProto>

    @GET("token/{token}")
    suspend fun findByToken(
        @Path("token") token: String,
    ): Response<ProfileResponseProto>
}
