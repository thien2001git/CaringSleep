package com.hoang.auth.domain.entity

import com.hoang.common.domain.entity.BaseIdEntity
import com.hoang.common.domain.entity.UserId

data class Profile(
    override val id: String? = null,
    val email: String = "",
    val displayName: String = "",
    val token: String? = null,
    val role: UserRole = UserRole.USER,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    override val userId: String = "",
) : BaseIdEntity<String>,
    UserId
