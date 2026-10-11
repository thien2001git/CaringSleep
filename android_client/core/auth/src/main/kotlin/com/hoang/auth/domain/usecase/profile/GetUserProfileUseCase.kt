package com.hoang.auth.domain.usecase.profile

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUserProfileUseCase
    @Inject
    constructor(
        private val profileRepository: Map<DataSource, @JvmSuppressWildcards BaseCrudRepository<Profile, String>>,
    ) : BaseUseCase<GetUserProfileUseCase.Params, Flow<Profile?>>() {
        override suspend fun doTask(params: Params): Flow<Profile?> =
            profileRepository[params.dataSource]?.getById(params.id) ?: flowOf(null)

        data class Params(
            val id: String,
            val dataSource: DataSource = DataSource.FireBase,
        ) : BaseUseCase.Params()
    }
