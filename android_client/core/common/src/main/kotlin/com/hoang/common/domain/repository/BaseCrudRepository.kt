package com.hoang.common.domain.repository

import com.hoang.common.domain.entity.Page
import kotlinx.coroutines.flow.Flow

interface BaseCrudRepository<T, Id> : BaseRepository {
    suspend fun insert(data: T): Result<Unit>

    suspend fun insertAll(data: List<T>): Result<Unit>

    fun getById(id: Id): Flow<T?>

    suspend fun getPage(
        pageNumber: Int,
        pageSize: Int,
    ): Page<T>

    suspend fun search(
        q: String,
        pageNumber: Int,
        pageSize: Int,
    ): Page<T>

    suspend fun getAll(): Flow<List<T>>

    suspend fun delete(id: Id): Result<Unit>

    suspend fun deleteAll(ids: List<Id>): Result<Unit>
}
