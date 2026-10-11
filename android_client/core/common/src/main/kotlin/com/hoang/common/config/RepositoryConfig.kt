package com.hoang.common.config

import com.hoang.common.data.local.pref.DataSourcePref
import com.hoang.common.data.repository.DataSourceRepositoryImpl
import com.hoang.common.domain.repository.DataSourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryConfig : BaseConfig {
    @Provides
    @Singleton
    fun provideDataSourceRepository(pref: DataSourcePref): DataSourceRepository = DataSourceRepositoryImpl(pref)
}
