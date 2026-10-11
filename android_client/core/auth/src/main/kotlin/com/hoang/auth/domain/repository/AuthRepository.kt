package com.hoang.auth.domain.repository

import com.hoang.auth.domain.entity.Profile
import com.hoang.common.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow

interface AuthRepository : BaseRepository {
    fun isLoggedIn(): Flow<Boolean>

    fun login(
        username: String,
        password: String,
    ): Result<Profile>

    fun register(
        username: String,
        password: String,
    ): Result<Profile>

    fun googleLogin(idToken: String? = null): Result<Profile>

    fun logout(): Result<Unit>
}
