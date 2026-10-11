package com.hoang.auth.domain.usecase.auth

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.common.domain.entity.getOrThrow
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class GoogleLoginUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val saveAuthSessionUseCase: SaveAuthSessionUseCase,
    ) : BaseUseCase<GoogleLoginUseCase.Params, Profile>() {
        override suspend fun doTask(params: Params): Profile {
            val authProfile = authRepository.googleLogin(params.idToken).getOrThrow()
            return saveAuthSessionUseCase(SaveAuthSessionUseCase.Params(authProfile)).getOrThrow()
        }

        data class Params(
            val idToken: String? = null,
        ) : BaseUseCase.Params()
    }
