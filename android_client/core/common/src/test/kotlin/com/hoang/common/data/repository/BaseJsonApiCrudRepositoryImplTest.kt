package com.hoang.common.data.repository

import com.hoang.common.data.remote.retrofit.BaseJsonCrudApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

data class TestEntity(
    val id: String,
    val name: String,
)

data class TestRemote(
    val id: String,
    val name: String,
)

class BaseJsonApiCrudRepositoryImplTest {
    private val apiService: BaseJsonCrudApiService<TestRemote> = mockk()

    private val repository =
        object : BaseJsonApiCrudRepositoryImpl<TestEntity, String, TestRemote>(apiService) {
            override fun toRemote(data: TestEntity): TestRemote = TestRemote(data.id, data.name)

            override fun toDomain(data: TestRemote): TestEntity = TestEntity(data.id, data.name)

            override fun toId(id: String): String = id
        }

    @Test
    fun `insert success`() =
        runTest {
            val entity = TestEntity("1", "Item 1")
            val remote = TestRemote("1", "Item 1")
            coEvery { apiService.insert(remote) } returns Response.success(remote)

            val result = repository.insert(entity)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `getById success`() =
        runTest {
            val remote = TestRemote("1", "Item 1")
            coEvery { apiService.getById("1") } returns Response.success(remote)

            val item = repository.getById("1").first()
            assertEquals(TestEntity("1", "Item 1"), item)
        }

    @Test
    fun `getAll success`() =
        runTest {
            val list = listOf(TestRemote("1", "Item 1"), TestRemote("2", "Item 2"))
            coEvery { apiService.getAll() } returns Response.success(list)

            val items = repository.getAll().first()
            assertEquals(2, items.size)
            assertEquals("Item 1", items[0].name)
            assertEquals("Item 2", items[1].name)
        }

    @Test
    fun `delete success`() =
        runTest {
            coEvery { apiService.delete("1") } returns Response.success(Unit)

            val result = repository.delete("1")
            assertTrue(result.isSuccess)
        }
}
