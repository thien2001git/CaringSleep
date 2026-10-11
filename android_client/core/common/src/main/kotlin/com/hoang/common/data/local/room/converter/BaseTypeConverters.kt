package com.hoang.common.data.local.room.converter
import com.google.gson.Gson

abstract class BaseTypeConverters<F, T> {
    protected val gson = Gson()

    abstract fun from(data: F): T

    abstract fun to(data: T): F
}
