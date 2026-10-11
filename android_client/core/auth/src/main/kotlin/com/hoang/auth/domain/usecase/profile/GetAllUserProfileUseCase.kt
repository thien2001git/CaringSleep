package com.hoang.auth.domain.usecase.profile

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAllUserProfileUseCase
    @Inject
    constructor(
        private val profileRepository: Map<DataSource, @JvmSuppressWildcards BaseCrudRepository<Profile, String>>,
    ) : BaseUseCase<GetAllUserProfileUseCase.Params, Flow<List<Profile>>>() {
        override suspend fun doTask(params: Params): Flow<List<Profile>> =
            profileRepository[params.dataSource]?.getAll() ?: flowOf(emptyList())

        data class Params(
            val dataSource: DataSource = DataSource.FireBase,
        ) : BaseUseCase.Params()
    }
