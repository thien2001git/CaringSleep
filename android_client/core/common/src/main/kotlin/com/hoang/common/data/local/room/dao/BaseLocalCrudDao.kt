package com.hoang.common.data.local.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

abstract class BaseLocalCrudDao<T>(
    private val tableName: String,
) {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: T): Result<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(data: List<T>): Result<Unit>

    @RawQuery
    protected abstract fun getByIdRaw(query: SupportSQLiteQuery): Flow<T?>

    fun getById(id: String): Flow<T?> =
        getByIdRaw(SimpleSQLiteQuery("SELECT * FROM $tableName WHERE id = '$id' LIMIT 1"))

    abstract fun getAll(): Flow<List<T>>

    @RawQuery
    protected abstract fun deleteByIdRaw(query: SupportSQLiteQuery): Int

    fun delete(id: String) =
        runCatching {
            deleteByIdRaw(SimpleSQLiteQuery("DELETE FROM $tableName WHERE id = '$id'"))
            Unit
        }

    fun deleteAll(ids: List<String>): Result<Unit> =
        runCatching {
            if (ids.isEmpty()) return@runCatching // SQLite crashes on empty IN () clauses

            deleteAllRaw(
                SimpleSQLiteQuery(
                    buildString {
                        append("DELETE FROM $tableName WHERE id IN (")
                        // Generate placeholders: "?, ?, ?"
                        append(ids.joinToString(",") { "?" })
                        append(")")
                    },
                    // Pass the actual ID values to bind to the placeholders
                    ids.toTypedArray(),
                ),
            )
        }

    @RawQuery
    protected abstract fun deleteAllRaw(query: SupportSQLiteQuery): Int
}
