package com.hoang.common.data.repository

import com.google.protobuf.MessageLite
import com.hoang.common.data.remote.retrofit.BaseProtoCrudApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

data class TestProtoEntity(
    val id: String,
    val name: String,
)

interface TestProtoRemote : MessageLite {
    val id: String
    val name: String
}

class BaseProtoApiCrudRepositoryTest {
    private val apiService: BaseProtoCrudApiService<TestProtoRemote> = mockk()

    private fun createRemote(
        id: String,
        name: String,
    ): TestProtoRemote {
        val remote: TestProtoRemote = mockk()
        coEvery { remote.id } returns id
        coEvery { remote.name } returns name
        return remote
    }

    private val repository =
        object : BaseProtoApiCrudRepositoryImpl<TestProtoEntity, String, TestProtoRemote>(apiService) {
            override fun toRemote(data: TestProtoEntity): TestProtoRemote = createRemote(data.id, data.name)

            override fun toDomain(data: TestProtoRemote): TestProtoEntity = TestProtoEntity(data.id, data.name)

            override fun toId(id: String): String = id

            override fun toBatchRemote(remotes: List<TestProtoRemote>): MessageLite = mockk()

            override fun toIdsMessage(ids: List<String>): MessageLite = mockk()
        }

    @Test
    fun `insert success`() =
        runTest {
            val entity = TestProtoEntity("1", "Item 1")
            val remote = createRemote("1", "Item 1")
            coEvery { apiService.insert(any()) } returns Response.success(remote)

            val result = repository.insert(entity)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `getById success`() =
        runTest {
            val remote = createRemote("1", "Item 1")
            coEvery { apiService.getById("1") } returns Response.success(remote)

            val item = repository.getById("1").first()
            assertEquals(TestProtoEntity("1", "Item 1"), item)
        }

    @Test
    fun `getAll success`() =
        runTest {
            val list = listOf(createRemote("1", "Item 1"), createRemote("2", "Item 2"))
            @Suppress("UNCHECKED_CAST")
            coEvery { apiService.getAll() } returns (Response.success(list) as Response<TestProtoRemote>)

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
