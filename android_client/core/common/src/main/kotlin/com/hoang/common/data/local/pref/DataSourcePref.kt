package com.hoang.common.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hoang.common.data.local.pref.BasePref
import com.hoang.common.domain.entity.DataSource

class DataSourcePref(
    dataStore: DataStore<Preferences>,
) : BasePref<String>(dataStore) {
    override val key: Preferences.Key<String> = stringPreferencesKey("DataSource")
    override val defaultValue: String? = gson.toJson(setOf(DataSource.FireBase, DataSource.Local))
}
