package com.hoang.newpipe.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.hoang.common.config.BaseConfig
import com.hoang.common.data.local.pref.DataSourcePref
import com.hoang.newpipe.data.local.pref.RestrictedModePref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.newPipeDataStore: DataStore<Preferences> by preferencesDataStore(name = "new_pipe_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataConfig : BaseConfig {
    @Provides
    @Singleton
    fun provideRestrictedModePref(
        @ApplicationContext context: Context,
    ): RestrictedModePref = RestrictedModePref(context.newPipeDataStore)
}
