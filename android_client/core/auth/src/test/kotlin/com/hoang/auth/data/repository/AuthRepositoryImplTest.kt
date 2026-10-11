package com.hoang.auth.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthRepositoryImplTest {
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val authRepository = AuthRepositoryImpl(firebaseAuth)

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `isLoggedIn returns true when currentUser is not null`() =
        runTest {
            val user: FirebaseUser = mockk()
            every { firebaseAuth.currentUser } returns user

            assertTrue(authRepository.isLoggedIn().first())
        }

    @Test
    fun `isLoggedIn returns false when currentUser is null`() =
        runTest {
            every { firebaseAuth.currentUser } returns null

            assertFalse(authRepository.isLoggedIn().first())
        }

    @Test
    fun `googleLogin with null idToken uses currentUser when available`() {
        val firebaseUser: FirebaseUser =
            mockk {
                every { uid } returns "uid_123"
                every { email } returns "user@gmail.com"
                every { displayName } returns "Google User"
                every { photoUrl } returns null
            }
        every { firebaseAuth.currentUser } returns firebaseUser

        val result = authRepository.googleLogin(null)

        assertTrue(result.isSuccess)
        val profile = result.getOrNull()
        assertEquals("uid_123", profile?.id)
        assertEquals("user@gmail.com", profile?.email)
        assertEquals("Google User", profile?.displayName)
    }

    @Test
    fun `googleLogin with null idToken fails when currentUser is null`() {
        every { firebaseAuth.currentUser } returns null

        val result = authRepository.googleLogin(null)

        assertTrue(result.isFailure)
        assertEquals("No authenticated Firebase user found for Google login", result.exceptionOrNull()?.message)
    }

    @Test
    fun `logout signs out firebaseAuth`() {
        val result = authRepository.logout()

        assertTrue(result.isSuccess)
    }
}
