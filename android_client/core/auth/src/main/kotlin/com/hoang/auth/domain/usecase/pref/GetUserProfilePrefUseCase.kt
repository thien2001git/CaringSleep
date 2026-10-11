package com.hoang.auth.domain.usecase.pref

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfilePrefRepository
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfilePrefUseCase
    @Inject
    constructor(
        private val profilePrefRepository: ProfilePrefRepository,
    ) : BaseUseCase<GetUserProfilePrefUseCase.Params, Flow<Profile?>>() {
        override suspend fun doTask(params: Params): Flow<Profile?> = profilePrefRepository.get()

        object Params : BaseUseCase.Params()
    }
