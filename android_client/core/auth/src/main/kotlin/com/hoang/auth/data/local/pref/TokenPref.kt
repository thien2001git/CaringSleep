package com.hoang.auth.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hoang.common.data.local.pref.BasePref

class TokenPref(
    dataStore: DataStore<Preferences>,
) : BasePref<String>(dataStore) {
    override val key: Preferences.Key<String> = stringPreferencesKey("Token")
    override val defaultValue: String? = null
}
