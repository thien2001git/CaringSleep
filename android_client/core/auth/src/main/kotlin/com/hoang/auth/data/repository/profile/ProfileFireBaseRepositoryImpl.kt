package com.hoang.auth.data.repository.profile

import com.google.firebase.firestore.CollectionReference
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfileRepository
import com.hoang.common.data.repository.BaseFireBaseCrudRepositoryImpl
import com.hoang.common.domain.provider.UserIdProvider

class ProfileFireBaseRepositoryImpl(
    collectionRef: CollectionReference,
    userIdProvider: UserIdProvider,
) : BaseFireBaseCrudRepositoryImpl<Profile, String>(collectionRef, userIdProvider, Profile::class.java),
    ProfileRepository {
    override fun getId(id: String): String = id

    override fun getId(data: Profile): String? = data.id

    override val searchFields = listOf("displayName", "email")
}
