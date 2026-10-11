package com.hoang.auth.domain.usecase.token

import com.hoang.auth.domain.repository.TokenRepository
import com.hoang.common.domain.usecase.BaseUseCase
import javax.inject.Inject

class SaveTokenUseCase
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
    ) : BaseUseCase<SaveTokenUseCase.Params, Unit>() {
        override suspend fun doTask(params: Params) = tokenRepository.set(params.token)

        data class Params(
            val token: String?,
        ) : BaseUseCase.Params()
    }
