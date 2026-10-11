package com.hoang.auth.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.common.utils.AppLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    ) : AuthRepository {
        override fun isLoggedIn(): Flow<Boolean> =
            callbackFlow {
                trySend(firebaseAuth.currentUser != null)
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        trySend(auth.currentUser != null)
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        override fun login(
            username: String,
            password: String,
        ): Result<Profile> =
            runCatching {
                val authResult = Tasks.await(firebaseAuth.signInWithEmailAndPassword(username, password))
                val firebaseUser = checkNotNull(authResult.user) { "Firebase user is null after login" }
                val token = runCatching { Tasks.await(firebaseUser.getIdToken(false)).token }.getOrNull()
                toUserProfile(firebaseUser, token)
            }

        override fun register(
            username: String,
            password: String,
        ): Result<Profile> =
            runCatching {
                val authResult = Tasks.await(firebaseAuth.createUserWithEmailAndPassword(username, password))
                val firebaseUser = checkNotNull(authResult.user) { "Firebase user is null after registration" }
                val token = runCatching { Tasks.await(firebaseUser.getIdToken(false)).token }.getOrNull()
                toUserProfile(firebaseUser, token)
            }

        override fun googleLogin(idToken: String?): Result<Profile> =
            runCatching {
                AppLog.d("Starting Google login in AuthRepositoryImpl (idToken present: ${!idToken.isNullOrEmpty()})")
                val firebaseUser =
                    if (!idToken.isNullOrEmpty()) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = Tasks.await(firebaseAuth.signInWithCredential(credential))
                        checkNotNull(authResult.user) { "Firebase user is null after Google login" }
                    } else {
                        checkNotNull(
                            firebaseAuth.currentUser,
                        ) { "No authenticated Firebase user found for Google login" }
                    }
                val token = runCatching { Tasks.await(firebaseUser.getIdToken(false)).token }.getOrNull()
                AppLog.i("Google login successful for user uid: ${firebaseUser.uid}")
                toUserProfile(firebaseUser, token)
            }.onFailure {
                AppLog.e("Google login failed in AuthRepositoryImpl", it)
            }

        override fun logout(): Result<Unit> =
            runCatching {
                firebaseAuth.signOut()
            }

        private fun toUserProfile(
            firebaseUser: FirebaseUser,
            token: String? = null,
        ): Profile =
            Profile(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: firebaseUser.email ?: "",
                avatarUrl = firebaseUser.photoUrl?.toString(),
                token = token,
                userId = firebaseUser.uid,
            )
    }
