package com.hoang.common.domain.entity

// 1. Add 'out' to the generic type
sealed class Resource<out T> : BaseEntity {
    data class Success<T>(
        val data: T,
    ) : Resource<T>()

    // 2. Change Unit to Nothing
    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : Resource<Nothing>()
}

fun <T> Resource<T>.getOrThrow(): T =
    when (this) {
        is Resource.Success -> data
        is Resource.Error -> throw throwable ?: Exception(message)
    }
