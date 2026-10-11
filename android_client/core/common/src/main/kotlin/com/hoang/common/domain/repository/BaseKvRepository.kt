package com.hoang.common.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseKvRepository<T> : BaseRepository {
    fun get(): Flow<T?>

    suspend fun set(value: T?)
}
