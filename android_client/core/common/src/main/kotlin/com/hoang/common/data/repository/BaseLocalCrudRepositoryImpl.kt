package com.hoang.common.data.repository

import com.hoang.common.data.local.room.dao.BaseLocalCrudDao
import com.hoang.common.domain.repository.BaseCrudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class BaseLocalCrudRepositoryImpl<T, Id, Local>(
    private val dao: BaseLocalCrudDao<Local>,
) : BaseCrudRepository<T, Id> {
    abstract fun toLocal(data: T): Local

    abstract fun toDomain(data: Local): T

    abstract fun toId(data: Id): String

    override suspend fun insert(data: T): Result<Unit> = dao.insert(toLocal(data))

    override suspend fun insertAll(data: List<T>): Result<Unit> = dao.insertAll(data.map { toLocal(it) })

    override fun getById(id: Id): Flow<T?> = dao.getById(toId(id)).map { it?.let { toDomain(it) } }

    override suspend fun getAll(): Flow<List<T>> = dao.getAll().map { it.map { x -> toDomain(x) } }

    override suspend fun delete(id: Id): Result<Unit> = dao.delete(toId(id))

    override suspend fun deleteAll(ids: List<Id>): Result<Unit> = dao.deleteAll(ids.map { toId(it) })
}
