package com.hoang.auth.data.repository.profile

import com.hoang.auth.data.remote.retrofit.ProfileProtoApiService
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.entity.UserRole
import com.hoang.moneycare.core.auth.presentation.proto.UserRoleProto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class ProfileProtoRepositoryImplTest {
    private val apiService: ProfileProtoApiService = mockk()
    private val repository = ProfileProtoRepositoryImpl(apiService)

    @Test
    fun `mapping domain to remote and back`() {
        val profile =
            Profile(
                id = "123",
                email = "user@example.com",
                displayName = "User Name",
                token = "token123",
                role = UserRole.ADMIN,
                avatarUrl = "https://example.com/avatar.png",
                createdAt = 1000L,
                updatedAt = 2000L,
                userId = "123",
            )

        val proto = repository.toRemote(profile)
        assertEquals("123", proto.id)
        assertEquals("user@example.com", proto.email)
        assertEquals("User Name", proto.displayName)
        assertEquals(UserRoleProto.ADMIN, proto.role)

        val mappedBack = repository.toDomain(proto)
        assertEquals(profile, mappedBack)
    }

    @Test
    fun `insert profile success`() =
        runTest {
            val profile = Profile(id = "p1", email = "test@example.com", displayName = "Test User")
            val proto = repository.toRemote(profile)
            coEvery { apiService.insert(any()) } returns Response.success(proto)

            val result = repository.insert(profile)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `getById profile success`() =
        runTest {
            val profile = Profile(id = "p1", email = "test@example.com", displayName = "Test User", userId = "p1")
            val proto = repository.toRemote(profile)
            coEvery { apiService.getById("p1") } returns Response.success(proto)

            val item = repository.getById("p1").first()
            assertEquals(profile, item)
        }
}
