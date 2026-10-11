package com.hoang.auth.data.provider

import com.google.firebase.auth.FirebaseAuth
import com.hoang.common.domain.provider.UserIdProvider
import javax.inject.Inject

class UserIdProviderImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
    ) : UserIdProvider {
        override fun getCurrentUserId(): String =
            firebaseAuth.currentUser?.uid
                ?: error("User is not logged in!")
    }
