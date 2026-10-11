package com.hoang.newpipe.data.repository

import com.hoang.common.data.repository.BaseKvRepositoryImpl
import com.hoang.newpipe.data.local.pref.RestrictedModePref
import com.hoang.newpipe.domain.repository.RestrictedModeRepository

class RestrictedModeRepositoryImpl(
    pref: RestrictedModePref,
) : BaseKvRepositoryImpl<Boolean, Boolean>(pref), RestrictedModeRepository {
    override fun toDomain(data: Boolean): Boolean = data

    override fun toEntity(data: Boolean): Boolean = data
}
