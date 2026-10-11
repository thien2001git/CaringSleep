package com.hoang.auth.domain.usecase.token

import com.hoang.auth.domain.repository.TokenRepository
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTokenUseCase
    @Inject
    constructor(
        private val tokenRepository: TokenRepository,
    ) : BaseUseCase<GetTokenUseCase.Params, Flow<String?>>() {
        override suspend fun doTask(params: Params): Flow<String?> = tokenRepository.get()

        object Params : BaseUseCase.Params()
    }
