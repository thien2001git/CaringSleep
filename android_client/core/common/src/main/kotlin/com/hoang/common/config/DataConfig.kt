package com.hoang.common.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.hoang.common.data.local.pref.DataSourcePref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.commonDataStore: DataStore<Preferences> by preferencesDataStore(name = "common_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataConfig : BaseConfig {
    @Provides
    @Singleton
    fun provideDataSourcePref(
        @ApplicationContext context: Context,
    ): DataSourcePref = DataSourcePref(context.commonDataStore)

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
