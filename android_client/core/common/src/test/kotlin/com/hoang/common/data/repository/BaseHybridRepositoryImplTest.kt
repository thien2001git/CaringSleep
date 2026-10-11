package com.hoang.common.data.repository

import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.entity.Page
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.repository.DataSourceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BaseHybridRepositoryImplTest {
    private val localRepo: BaseCrudRepository<TestEntity, String> = mockk(relaxed = true)
    private val fireBaseRepo: BaseCrudRepository<TestEntity, String> = mockk(relaxed = true)
    private val apiRepo: BaseCrudRepository<TestEntity, String> = mockk(relaxed = true)
    private val dataSourceRepository: DataSourceRepository = mockk(relaxed = true)

    private val repository =
        object : BaseHybridRepositoryImpl<TestEntity, String>(
            mapOf(
                DataSource.Local to localRepo,
                DataSource.FireBase to fireBaseRepo,
                DataSource.SpringJson to apiRepo,
            ),
            dataSourceRepository,
        ) {
            override fun getId(data: TestEntity): String = data.id

            override fun isLocalNewer(
                local: TestEntity?,
                remote: TestEntity?,
            ): Boolean =
                when {
                    local == null -> false
                    remote == null -> true
                    else -> local.name.length >= remote.name.length
                }

            override suspend fun getPage(
                pageNumber: Int,
                pageSize: Int,
            ): Page<TestEntity> = Page.empty()

            override suspend fun search(
                q: String,
                pageNumber: Int,
                pageSize: Int,
            ): Page<TestEntity> = Page.empty()
        }

    @Test
    fun `insert delegates to all repositories`() =
        runTest {
            val entity = TestEntity("1", "Item 1")
            coEvery { apiRepo.insert(entity) } returns Result.success(Unit)
            coEvery { fireBaseRepo.insert(entity) } returns Result.success(Unit)
            coEvery { localRepo.insert(entity) } returns Result.success(Unit)
            coEvery { dataSourceRepository.get() } returns flowOf(DataSource.entries.toSet())
            val result = repository.insert(entity)
            assertTrue(result.isSuccess)

            coVerify {
                apiRepo.insert(entity)
                fireBaseRepo.insert(entity)
                localRepo.insert(entity)
            }
        }

    @Test
    fun `getById selects newest item across data sources`() =
        runTest {
            val localEntity = TestEntity("1", "Short")
            val remoteEntity = TestEntity("1", "Longer Name")
            coEvery { localRepo.getById("1") } returns flowOf(localEntity)
            coEvery { fireBaseRepo.getById("1") } returns flowOf(null)
            coEvery { apiRepo.getById("1") } returns flowOf(remoteEntity)

            val item = repository.getById("1").first()
            assertEquals(remoteEntity, item)
        }

    @Test
    fun `uploadAll uploads local data to both api and firebase`() =
        runTest {
            val localList = listOf(TestEntity("1", "Local 1"))
            coEvery { localRepo.getAll() } returns flowOf(localList)
            coEvery { apiRepo.insertAll(localList) } returns Result.success(Unit)
            coEvery { fireBaseRepo.insertAll(localList) } returns Result.success(Unit)

            val result = repository.uploadAll()
            assertTrue(result.isSuccess)

            coVerify {
                apiRepo.insertAll(localList)
                fireBaseRepo.insertAll(localList)
            }
        }

    @Test
    fun `fetchAll fetches remote data into local repository`() =
        runTest {
            val remoteList = listOf(TestEntity("1", "Remote 1"))
            coEvery { fireBaseRepo.getAll() } returns flowOf(remoteList)
            coEvery { apiRepo.getAll() } returns flowOf(emptyList())
            coEvery { localRepo.insertAll(remoteList) } returns Result.success(Unit)

            val result = repository.fetchAll()
            assertTrue(result.isSuccess)

            coVerify {
                localRepo.insertAll(remoteList)
            }
        }
}
