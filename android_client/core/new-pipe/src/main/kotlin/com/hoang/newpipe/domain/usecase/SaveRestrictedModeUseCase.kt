package com.hoang.newpipe.domain.usecase

import com.hoang.common.domain.usecase.BaseUseCase
import com.hoang.newpipe.domain.repository.RestrictedModeRepository
import javax.inject.Inject

class SaveRestrictedModeUseCase
    @Inject
    constructor(
        private val restrictedModeRepository: RestrictedModeRepository,
    ) : BaseUseCase<SaveRestrictedModeUseCase.Params, Unit>() {
        override suspend fun doTask(params: Params) = restrictedModeRepository.set(params.isRestrictedMode)

        data class Params(
            val isRestrictedMode: Boolean?,
        ) : BaseUseCase.Params()
    }
