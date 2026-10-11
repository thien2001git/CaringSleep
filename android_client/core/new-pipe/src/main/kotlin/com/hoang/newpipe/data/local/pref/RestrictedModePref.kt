package com.hoang.newpipe.data.local.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.hoang.common.data.local.pref.BasePref

class RestrictedModePref(
    dataStore: DataStore<Preferences>,
) : BasePref<Boolean>(dataStore) {
    override val key: Preferences.Key<Boolean> = booleanPreferencesKey("RestrictedMode")
    override val defaultValue: Boolean = false
}
