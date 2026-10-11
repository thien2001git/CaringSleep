package com.hoang.auth.data.repository.profile

import com.hoang.auth.data.remote.retrofit.ProfileJsonApiService
import com.hoang.auth.domain.entity.Profile
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class ProfileJsonRepositoryImplTest {
    private val apiService: ProfileJsonApiService = mockk()
    private val repository = ProfileJsonRepositoryImpl(apiService)

    @Test
    fun `insert profile success`() =
        runTest {
            val profile = Profile(id = "p1", email = "test@example.com", displayName = "Test User", userId = "p1")
            coEvery { apiService.insert(profile) } returns Response.success(profile)

            val result = repository.insert(profile)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `getById profile success`() =
        runTest {
            val profile = Profile(id = "p1", email = "test@example.com", displayName = "Test User", userId = "p1")
            coEvery { apiService.getById("p1") } returns Response.success(profile)

            val item = repository.getById("p1").first()
            assertEquals(profile, item)
        }
}
