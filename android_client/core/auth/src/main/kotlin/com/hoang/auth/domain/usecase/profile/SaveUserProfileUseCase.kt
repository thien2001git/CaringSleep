package com.hoang.auth.domain.usecase.profile

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class SaveUserProfileUseCase
    @Inject
    constructor(
        private val profileRepository: Map<DataSource, @JvmSuppressWildcards BaseCrudRepository<Profile, String>>,
    ) : BaseUseCase<SaveUserProfileUseCase.Params, Result<Unit>>() {
        override suspend fun doTask(params: Params): Result<Unit> =
            profileRepository[params.dataSource]?.insert(params.profile)
                ?: Result.failure(Exception("Data source not supported"))

        data class Params(
            val profile: Profile,
            val dataSource: DataSource = DataSource.FireBase,
        ) : BaseUseCase.Params()
    }
