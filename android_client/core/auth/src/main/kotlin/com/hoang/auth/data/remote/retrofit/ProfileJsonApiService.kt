package com.hoang.auth.data.remote.retrofit

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.data.remote.retrofit.BaseJsonCrudApiService
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileJsonApiService : BaseJsonCrudApiService<Profile> {
    @GET("email/{email}")
    suspend fun findByEmail(
        @Path("email") email: String,
    ): Response<Profile?>

    @GET("token/{token}")
    suspend fun findByToken(
        @Path("token") token: String,
    ): Response<Profile?>

    @GET("all")
    override suspend fun getAll(): Response<List<Profile>>
}
