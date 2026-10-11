package com.hoang.newpipe.domain.usecase

import com.hoang.common.domain.usecase.BaseUseCase
import com.hoang.newpipe.domain.repository.RestrictedModeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRestrictedModeUseCase
    @Inject
    constructor(
        private val restrictedModeRepository: RestrictedModeRepository,
    ) : BaseUseCase<GetRestrictedModeUseCase.Params, Flow<Boolean?>>() {
        override suspend fun doTask(params: Params): Flow<Boolean?> = restrictedModeRepository.get()

        object Params : BaseUseCase.Params()
    }
