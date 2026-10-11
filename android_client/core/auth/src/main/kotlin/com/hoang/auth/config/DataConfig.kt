package com.hoang.auth.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.hoang.auth.data.local.pref.ProfilePref
import com.hoang.auth.data.local.pref.TokenPref
import com.hoang.common.config.BaseConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataConfig : BaseConfig {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.authDataStore

    @Provides
    @Singleton
    fun provideTokenPref(dataStore: DataStore<Preferences>): TokenPref = TokenPref(dataStore)

    @Provides
    @Singleton
    fun provideProfilePref(dataStore: DataStore<Preferences>): ProfilePref = ProfilePref(dataStore)
}
