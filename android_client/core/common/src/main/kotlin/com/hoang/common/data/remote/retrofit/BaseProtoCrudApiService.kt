package com.hoang.common.data.remote.retrofit

import com.google.protobuf.MessageLite
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Add <Res> so Retrofit knows which proto object to construct for the response
interface BaseProtoCrudApiService<Res : MessageLite> : BaseRemoteApiService {
    @POST("")
    suspend fun insert(
        @Body data: MessageLite,
    ): Response<Res>

    @POST("batch")
    suspend fun insertAll(
        @Body data: MessageLite,
    ): Response<Res>

    @GET("{id}")
    suspend fun getById(
        @Path("id") id: String,
    ): Response<Res>

    @GET("all")
    suspend fun getAll(): Response<Res>

    @GET("page")
    suspend fun getPage(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<Res>

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<Res>

    @DELETE("{id}")
    suspend fun delete(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("delete-batch")
    suspend fun deleteAll(
        @Body ids: MessageLite,
    ): Response<Unit>
}
