package com.hoang.auth.data.repository

import com.hoang.auth.data.local.pref.TokenPref
import com.hoang.auth.domain.repository.TokenRepository
import com.hoang.common.data.repository.BaseKvRepositoryImpl

class TokenRepositoryImpl(
    pref: TokenPref,
) : BaseKvRepositoryImpl<String, String>(pref),
    TokenRepository {
    override fun toDomain(data: String): String = data

    override fun toEntity(data: String): String = data
}
