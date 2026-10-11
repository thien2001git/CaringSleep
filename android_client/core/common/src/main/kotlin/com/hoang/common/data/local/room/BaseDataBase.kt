package com.hoang.common.data.local.room

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.hoang.common.data.local.room.converter.BaseTypeConverters

abstract class BaseDataBase : RoomDatabase() {
    sealed class DatabaseConfig<T : BaseDataBase>(
        val databaseClass: Class<T>,
        val databaseName: String,
        val version: Int,
        val migrations: List<Migration> = emptyList(),
        val typeConverters: List<BaseTypeConverters<*, *>> = emptyList(),
    )
}
