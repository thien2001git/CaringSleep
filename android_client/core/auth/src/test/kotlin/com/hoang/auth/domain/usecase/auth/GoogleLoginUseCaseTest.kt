package com.hoang.auth.domain.usecase.auth

import android.util.Log
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.AuthRepository
import com.hoang.auth.domain.usecase.pref.SaveUserProfilePrefUseCase
import com.hoang.auth.domain.usecase.profile.GetUserProfileUseCase
import com.hoang.auth.domain.usecase.profile.SaveUserProfileUseCase
import com.hoang.auth.domain.usecase.token.SaveTokenUseCase
import com.hoang.common.domain.entity.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoogleLoginUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val saveTokenUseCase: SaveTokenUseCase = mockk(relaxed = true)
    private val saveUserProfileUseCase: SaveUserProfileUseCase = mockk(relaxed = true)
    private val saveUserProfilePrefUseCase: SaveUserProfilePrefUseCase = mockk(relaxed = true)
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()

    private val saveAuthSessionUseCase =
        SaveAuthSessionUseCase(
            saveTokenUseCase = saveTokenUseCase,
            saveUserProfileUseCase = saveUserProfileUseCase,
            saveUserProfilePrefUseCase = saveUserProfilePrefUseCase,
            getUserProfileUseCase = getUserProfileUseCase,
        )

    private val googleLoginUseCase =
        GoogleLoginUseCase(
            authRepository = authRepository,
            saveAuthSessionUseCase = saveAuthSessionUseCase,
        )

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        coEvery { Log.e(any(), any(), any()) } returns 0
        coEvery { Log.e(any(), any()) } returns 0
        coEvery { getUserProfileUseCase(any()) } returns Resource.Success(flowOf(null))
    }

    @Test
    fun `doTask when googleLogin succeeds saves profile and token`() =
        runTest {
            val profile =
                Profile(
                    id = "user123",
                    email = "user@example.com",
                    displayName = "Test User",
                    token = "id_token_123",
                )
            coEvery { authRepository.googleLogin("id_token_123") } returns Result.success(profile)

            val result = googleLoginUseCase(GoogleLoginUseCase.Params("id_token_123"))

            assertTrue(result is Resource.Success)
            assertEquals(profile, (result as Resource.Success).data)

            coVerify { saveTokenUseCase(SaveTokenUseCase.Params("id_token_123")) }
            coVerify { saveUserProfileUseCase(SaveUserProfileUseCase.Params(profile)) }
            coVerify { saveUserProfilePrefUseCase(SaveUserProfilePrefUseCase.Params(profile)) }
        }

    @Test
    fun `doTask when googleLogin fails returns Resource Error`() =
        runTest {
            val exception = RuntimeException("Google sign in failed")
            coEvery { authRepository.googleLogin(null) } returns Result.failure(exception)

            val result = googleLoginUseCase(GoogleLoginUseCase.Params(null))

            assertTrue(result is Resource.Error)
            assertEquals("Google sign in failed", (result as Resource.Error).message)
        }
}
