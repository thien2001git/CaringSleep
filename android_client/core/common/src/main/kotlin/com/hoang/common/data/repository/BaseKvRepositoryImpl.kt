package com.hoang.common.data.repository

import com.google.gson.Gson
import com.hoang.common.data.local.pref.BasePref
import com.hoang.common.domain.repository.BaseKvRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class BaseKvRepositoryImpl<T, K>(
    private val basePref: BasePref<K>,
) : BaseKvRepository<T> {
    protected val gson = Gson()

    override fun get(): Flow<T?> =
        basePref.get().map {
            it?.let { x -> toDomain(x) }
        }

    override suspend fun set(value: T?) {
        if (value != null) {
            basePref.set(toEntity(value))
        } else {
            basePref.clear()
        }
    }

    abstract fun toDomain(data: K): T

    abstract fun toEntity(data: T): K
}
