package com.hoang.common.data.remote.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BaseJsonCrudApiService<Remote> : BaseRemoteApiService {
    @POST(".")
    suspend fun insert(
        @Body data: Remote,
    ): Response<Remote>

    @POST("batch")
    suspend fun insertAll(
        @Body data: List<Remote>,
    ): Response<Unit>

    @GET("{id}")
    suspend fun getById(
        @Path("id") id: String,
    ): Response<Remote?>

    @GET("all")
    suspend fun getAll(): Response<List<Remote>>

    @GET("page")
    suspend fun getPage(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<*>

    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<*>

    @DELETE("{id}")
    suspend fun delete(
        @Path("id") id: String,
    ): Response<Unit>

    @POST("delete-batch")
    suspend fun deleteAll(
        @Body ids: List<String>,
    ): Response<Unit>
}
