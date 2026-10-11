package com.hoang.common.domain.provider

interface UserIdProvider : BaseProvider {
    fun getCurrentUserId(): String
}
