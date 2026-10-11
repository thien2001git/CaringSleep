package com.hoang.common.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class BasePref<T>(
    private val dataStore: DataStore<Preferences>,
) {
    protected val gson = Gson()
    abstract val key: Preferences.Key<T>
    abstract val defaultValue: T?

    fun get(): Flow<T?> =
        dataStore.data.map {
            it[key] ?: defaultValue
        }

    suspend fun set(value: T) =
        dataStore.edit { preferences ->
            preferences[key] = value
        }

    suspend fun clear() =
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
}
