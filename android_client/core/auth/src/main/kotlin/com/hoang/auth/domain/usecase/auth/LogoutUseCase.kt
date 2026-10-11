package com.hoang.auth.domain.usecase.auth

import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.auth.domain.usecase.pref.SaveUserProfilePrefUseCase
import com.hoang.auth.domain.usecase.token.SaveTokenUseCase
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val saveTokenUseCase: SaveTokenUseCase,
        private val saveUserProfilePrefUseCase: SaveUserProfilePrefUseCase,
    ) : BaseUseCase<LogoutUseCase.Params, Unit>() {
        override suspend fun doTask(params: Params) {
            authRepository.logout().getOrThrow()
            saveTokenUseCase(SaveTokenUseCase.Params(null))
            saveUserProfilePrefUseCase(SaveUserProfilePrefUseCase.Params(null))
        }

        object Params : BaseUseCase.Params()
    }
