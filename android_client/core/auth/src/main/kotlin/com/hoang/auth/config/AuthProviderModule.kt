package com.hoang.auth.config

import com.hoang.auth.data.provider.UserIdProviderImpl
import com.hoang.common.config.BaseConfig
import com.hoang.common.domain.provider.UserIdProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthProviderModule : BaseConfig {
    @Binds
    @Singleton
    abstract fun bindUserIdProvider(userIdProviderImpl: UserIdProviderImpl): UserIdProvider
}
