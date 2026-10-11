package com.hoang.auth.data.repository.profile

import com.hoang.auth.data.remote.retrofit.ProfileJsonApiService
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfileRepository
import com.hoang.common.data.repository.BaseJsonApiCrudRepositoryImpl

class ProfileJsonRepositoryImpl(
    apiService: ProfileJsonApiService? = null,
) : BaseJsonApiCrudRepositoryImpl<Profile, String, Profile>(apiService),
    ProfileRepository {
    override fun toRemote(data: Profile): Profile = data

    override fun toDomain(data: Profile): Profile = data

    override fun toId(id: String): String = id
}
