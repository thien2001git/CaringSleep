package com.hoang.auth.domain.usecase.auth

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.usecase.pref.SaveUserProfilePrefUseCase
import com.hoang.auth.domain.usecase.profile.GetUserProfileUseCase
import com.hoang.auth.domain.usecase.profile.SaveUserProfileUseCase
import com.hoang.auth.domain.usecase.token.SaveTokenUseCase
import com.hoang.common.domain.entity.Resource
import com.hoang.common.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveAuthSessionUseCase
    @Inject
    constructor(
        private val saveTokenUseCase: SaveTokenUseCase,
        private val saveUserProfileUseCase: SaveUserProfileUseCase,
        private val saveUserProfilePrefUseCase: SaveUserProfilePrefUseCase,
        private val getUserProfileUseCase: GetUserProfileUseCase,
    ) : BaseUseCase<SaveAuthSessionUseCase.Params, Profile>() {
        override suspend fun doTask(params: Params): Profile {
            val authProfile = params.authProfile
            val userProfile = resolveUserProfile(authProfile)

            saveTokenUseCase(SaveTokenUseCase.Params(userProfile.token))
            saveUserProfileUseCase(SaveUserProfileUseCase.Params(userProfile))
            saveUserProfilePrefUseCase(SaveUserProfilePrefUseCase.Params(userProfile))
            return userProfile
        }

        private suspend fun resolveUserProfile(authProfile: Profile): Profile {
            val userId = authProfile.id
            val serverProfile =
                if (userId != null) {
                    val profileRes = getUserProfileUseCase(GetUserProfileUseCase.Params(userId))
                    if (profileRes is Resource.Success) profileRes.data.first() else null
                } else {
                    null
                }

            return if (serverProfile != null) {
                mergeServerProfileWithToken(serverProfile, authProfile.token)
            } else {
                authProfile
            }
        }

        private fun mergeServerProfileWithToken(
            serverProfile: Profile,
            newToken: String?,
        ): Profile {
            val tokenChanged = serverProfile.token != newToken
            val newUpdatedAt = if (tokenChanged) System.currentTimeMillis() else serverProfile.updatedAt
            return serverProfile.copy(token = newToken, updatedAt = newUpdatedAt)
        }

        data class Params(
            val authProfile: Profile,
        ) : BaseUseCase.Params()
    }
