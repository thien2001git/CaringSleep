package com.hoang.auth.domain.usecase.auth

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.common.domain.entity.getOrThrow
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val saveAuthSessionUseCase: SaveAuthSessionUseCase,
    ) : BaseUseCase<LoginUseCase.Params, Profile>() {
        override suspend fun doTask(params: Params): Profile {
            val authProfile = authRepository.login(params.username, password = params.password).getOrThrow()
            return saveAuthSessionUseCase(SaveAuthSessionUseCase.Params(authProfile)).getOrThrow()
        }

        data class Params(
            val username: String,
            val password: String,
        ) : BaseUseCase.Params()
    }
