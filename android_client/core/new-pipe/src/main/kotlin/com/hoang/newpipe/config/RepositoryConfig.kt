package com.hoang.newpipe.config

import com.hoang.common.config.BaseConfig
import com.hoang.newpipe.data.local.pref.RestrictedModePref
import com.hoang.newpipe.data.repository.RestrictedModeRepositoryImpl
import com.hoang.newpipe.domain.repository.RestrictedModeRepository
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
    fun provideRestrictedModeRepository(pref: RestrictedModePref): RestrictedModeRepository =
        RestrictedModeRepositoryImpl(pref)
}
