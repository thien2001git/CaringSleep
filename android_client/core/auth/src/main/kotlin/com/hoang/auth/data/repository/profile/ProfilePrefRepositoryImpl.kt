package com.hoang.auth.data.repository.profile

import com.hoang.auth.data.local.pref.ProfilePref
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfilePrefRepository
import com.hoang.common.data.repository.BaseKvRepositoryImpl

class ProfilePrefRepositoryImpl(
    pref: ProfilePref,
) : BaseKvRepositoryImpl<Profile, String>(pref),
    ProfilePrefRepository {
    override fun toDomain(data: String): Profile = gson.fromJson(data, Profile::class.java)

    override fun toEntity(data: Profile): String = gson.toJson(data)
}
