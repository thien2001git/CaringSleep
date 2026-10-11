package com.hoang.common.domain.repository

interface BaseRemoteRepository : BaseRepository {
    suspend fun fetchAll(): Result<Unit>

    suspend fun uploadAll(): Result<Unit>
}
