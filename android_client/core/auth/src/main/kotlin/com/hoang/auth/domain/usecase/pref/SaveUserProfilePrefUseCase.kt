package com.hoang.auth.domain.usecase.pref

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfilePrefRepository
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class SaveUserProfilePrefUseCase
    @Inject
    constructor(
        private val profilePrefRepository: ProfilePrefRepository,
    ) : BaseUseCase<SaveUserProfilePrefUseCase.Params, Unit>() {
        override suspend fun doTask(params: Params) = profilePrefRepository.set(params.profile)

        data class Params(
            val profile: Profile?,
        ) : BaseUseCase.Params()
    }
