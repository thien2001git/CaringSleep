package com.hoang.auth.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hoang.auth.data.local.pref.ProfilePref
import com.hoang.auth.data.local.pref.TokenPref
import com.hoang.auth.data.repository.AuthRepositoryImpl
import com.hoang.auth.data.repository.TokenRepositoryImpl
import com.hoang.auth.data.repository.profile.ProfileFireBaseRepositoryImpl
import com.hoang.auth.data.repository.profile.ProfileHybridRepositoryImpl
import com.hoang.auth.data.repository.profile.ProfilePrefRepositoryImpl
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.auth.domain.repository.ProfilePrefRepository
import com.hoang.auth.domain.repository.ProfileRepository
import com.hoang.auth.domain.repository.TokenRepository
import com.hoang.common.config.BaseConfig
import com.hoang.common.data.repository.entity.FireBasePaths
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.provider.UserIdProvider
import com.hoang.common.domain.repository.BaseCrudRepository
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideTokenRepository(tokenPref: TokenPref): TokenRepository = TokenRepositoryImpl(tokenPref)

    @Provides
    @Singleton
    fun provideUserProfilePrefRepository(profilePref: ProfilePref): ProfilePrefRepository =
        ProfilePrefRepositoryImpl(profilePref)

    @Provides
    @Singleton
    fun provideProfileRepositories(
        firestore: FirebaseFirestore,
        userIdProvider: UserIdProvider,
    ): Map<DataSource, @JvmSuppressWildcards BaseCrudRepository<Profile, String>> =
        mapOf(
            DataSource.FireBase to
                ProfileFireBaseRepositoryImpl(firestore.collection(FireBasePaths.USER_PROFILES), userIdProvider),
        )

    @Provides
    @Singleton
    fun provideProfileRepository(
        crudRepositories: Map<DataSource, @JvmSuppressWildcards BaseCrudRepository<Profile, String>>,
        dataSourceRepository: DataSourceRepository,
    ): ProfileRepository = ProfileHybridRepositoryImpl(crudRepositories, dataSourceRepository)
}
