package com.hoang.common.data.repository

import com.google.protobuf.MessageLite
import com.hoang.common.data.remote.retrofit.BaseProtoCrudApiService
import com.hoang.common.domain.entity.Page
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.utils.AppLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

abstract class BaseProtoApiCrudRepositoryImpl<T, Id, Remote : MessageLite>(
    private val apiService: BaseProtoCrudApiService<Remote>? = null,
) : BaseCrudRepository<T, Id> {
    abstract fun toRemote(data: T): Remote

    abstract fun toDomain(data: Remote): T

    abstract fun toId(id: Id): String

    open fun toBatchRemote(remotes: List<Remote>): MessageLite =
        throw UnsupportedOperationException("toBatchRemote or executeInsertAll must be implemented")

    open fun toIdsMessage(ids: List<String>): MessageLite =
        throw UnsupportedOperationException("toIdsMessage or executeDeleteAll must be implemented")

    open fun toDomainList(remoteResponse: Any?): List<T> =
        when (remoteResponse) {
            is List<*> ->
                remoteResponse
                    .asSequence()
                    .filterIsInstance<MessageLite>()
                    .map {
                        @Suppress("UNCHECKED_CAST")
                        toDomain(it as Remote)
                    }.toList()
            is MessageLite -> {
                @Suppress("UNCHECKED_CAST")
                (remoteResponse as? Remote)?.let { listOf(toDomain(it)) } ?: emptyList()
            }
            else -> emptyList()
        }

    // NEW: Abstract mapping for pagination
    open fun toDomainPage(remoteResponse: Any?): Page<T> =
        throw UnsupportedOperationException("toDomainPage must be implemented to use getPage or search")

    open suspend fun executeInsert(remote: Remote): Response<*> =
        apiService?.insert(remote)
            ?: throw UnsupportedOperationException("apiService or executeInsert must be implemented")

    open suspend fun executeInsertAll(remotes: List<Remote>): Response<*> =
        apiService?.insertAll(toBatchRemote(remotes))
            ?: throw UnsupportedOperationException("apiService or executeInsertAll must be implemented")

    open suspend fun executeGetById(idStr: String): Response<*> =
        apiService?.getById(idStr)
            ?: throw UnsupportedOperationException("apiService or executeGetById must be implemented")

    open suspend fun executeGetAll(): Response<*> =
        apiService?.getAll()
            ?: throw UnsupportedOperationException("apiService or executeGetAll must be implemented")

    open suspend fun executeGetPage(
        page: Int,
        size: Int,
    ): Response<*> =
        apiService?.getPage(page, size)
            ?: throw UnsupportedOperationException("apiService or executeGetPage must be implemented")

    open suspend fun executeSearch(
        q: String,
        page: Int,
        size: Int,
    ): Response<*> =
        apiService?.search(q, page, size)
            ?: throw UnsupportedOperationException("apiService or executeSearch must be implemented")

    open suspend fun executeDelete(idStr: String): Response<*> =
        apiService?.delete(idStr)
            ?: throw UnsupportedOperationException("apiService or executeDelete must be implemented")

    open suspend fun executeDeleteAll(ids: List<String>): Response<*> =
        apiService?.deleteAll(toIdsMessage(ids))
            ?: throw UnsupportedOperationException("apiService or executeDeleteAll must be implemented")

    override suspend fun insert(data: T): Result<Unit> =
        runCatching {
            val response = executeInsert(toRemote(data))
            if (!response.isSuccessful) {
                error("API insert failed with code ${response.code()}: ${response.message()}")
            }
        }

    override suspend fun insertAll(data: List<T>): Result<Unit> =
        runCatching {
            val response = executeInsertAll(data.map { toRemote(it) })
            if (!response.isSuccessful) {
                error("API insertAll failed with code ${response.code()}: ${response.message()}")
            }
        }

    override fun getById(id: Id): Flow<T?> =
        flow {
            val response = executeGetById(toId(id))
            if (response.isSuccessful) {
                val domain =
                    when (val body = response.body()) {
                        is MessageLite -> {
                            @Suppress("UNCHECKED_CAST")
                            toDomain(body as Remote)
                        }
                        else -> null
                    }
                emit(domain)
            } else {
                emit(null)
            }
        }

    override suspend fun getPage(
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        runCatching {
            val response = executeGetPage(pageNumber, pageSize)
            if (response.isSuccessful) {
                toDomainPage(response.body())
            } else {
                AppLog.e("API getPage failed with code ${response.code()}: ${response.message()}")
                Page.empty()
            }
        }.getOrElse {
            AppLog.e("Exception in getPage", it)
            Page.empty()
        }

    override suspend fun search(
        q: String,
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        runCatching {
            val response = executeSearch(q, pageNumber, pageSize)
            if (response.isSuccessful) {
                toDomainPage(response.body())
            } else {
                AppLog.e("API search failed with code ${response.code()}: ${response.message()}")
                Page.empty()
            }
        }.getOrElse {
            AppLog.e("Exception in search", it)
            Page.empty()
        }

    override suspend fun getAll(): Flow<List<T>> =
        flow {
            val response = executeGetAll()
            if (response.isSuccessful) {
                val list = toDomainList(response.body())
                emit(list)
            } else {
                emit(emptyList())
            }
        }

    override suspend fun delete(id: Id): Result<Unit> =
        runCatching {
            val response = executeDelete(toId(id))
            if (!response.isSuccessful) {
                error("API delete failed with code ${response.code()}: ${response.message()}")
            }
        }

    override suspend fun deleteAll(ids: List<Id>): Result<Unit> =
        runCatching {
            val response = executeDeleteAll(ids.map { toId(it) })
            if (!response.isSuccessful) {
                error("API deleteAll failed with code ${response.code()}: ${response.message()}")
            }
        }
}
