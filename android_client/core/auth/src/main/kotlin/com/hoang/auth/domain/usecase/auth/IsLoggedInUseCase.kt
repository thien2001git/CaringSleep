package com.hoang.auth.domain.usecase.auth

import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsLoggedInUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : BaseUseCase<IsLoggedInUseCase.Params, Flow<Boolean>>() {
        override suspend fun doTask(params: Params): Flow<Boolean> = authRepository.isLoggedIn()

        object Params : BaseUseCase.Params()
    }
