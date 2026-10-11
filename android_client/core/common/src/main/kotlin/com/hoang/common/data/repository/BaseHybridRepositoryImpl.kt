package com.hoang.common.data.repository

import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.entity.Page
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.repository.BaseHybridRepository
import com.hoang.common.domain.repository.DataSourceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

abstract class BaseHybridRepositoryImpl<T, Id>(
    private val crudRepositories: Map<DataSource, BaseCrudRepository<T, Id>> = emptyMap(),
    private val dataSourceRepository: DataSourceRepository,
) : BaseHybridRepository<T, Id> {
    constructor(
        localCrudRepository: BaseCrudRepository<T, Id>,
        fireBaseCrudRepository: BaseCrudRepository<T, Id>,
        apiCrudRepository: BaseCrudRepository<T, Id>,
        dataSourceRepository: DataSourceRepository,
    ) : this(
        mapOf(
            DataSource.Local to localCrudRepository,
            DataSource.FireBase to fireBaseCrudRepository,
            DataSource.SpringJson to apiCrudRepository,
        ),
        dataSourceRepository,
    )

    abstract fun getId(data: T): Id?

    abstract fun isLocalNewer(
        local: T?,
        remote: T?,
    ): Boolean

    private suspend fun getRepos() =
        crudRepositories.filter {
            dataSourceRepository.get().first()?.contains(it.key) ==
                true
        }

    private fun selectNewest(candidates: List<T?>): T? {
        val nonNullCandidates = candidates.filterNotNull()
        if (nonNullCandidates.isEmpty()) return null
        var newest = nonNullCandidates.first()
        for (item in nonNullCandidates.drop(1)) {
            if (isLocalNewer(item, newest)) {
                newest = item
            }
        }
        return newest
    }

    override suspend fun insert(data: T): Result<Unit> =
        runCatching {
            for (repo in crudRepositories
                .filter {
                    dataSourceRepository.get().first()?.contains(
                        it.key,
                    ) == true
                }.values) {
                repo.insert(data).getOrThrow()
            }
        }

    override suspend fun insertAll(data: List<T>): Result<Unit> =
        runCatching {
            for (repo in crudRepositories.values) {
                repo.insertAll(data).getOrThrow()
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun getById(id: Id): Flow<T?> =
        if (crudRepositories.isEmpty()) {
            flowOf(null)
        } else {
            val flows = crudRepositories.values.map { it.getById(id) as Flow<Any?> }
            combine(flows) { array ->
                selectNewest(array.toList() as List<T?>)
            }
        }

    override suspend fun getPage(
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        coroutineScope {
            if (getRepos().isEmpty()) return@coroutineScope Page.empty()

            // Fetch pages concurrently across all data sources
            val deferredPages =
                getRepos().values.map { repo ->
                    async {
                        runCatching { repo.getPage(pageNumber, pageSize) }.getOrDefault(Page.empty())
                    }
                }

            mergePages(deferredPages.awaitAll())
        }

    override suspend fun search(
        q: String,
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        coroutineScope {
            if (getRepos().isEmpty()) return@coroutineScope Page.empty()

            // Fetch search results concurrently across all data sources
            val deferredPages =
                getRepos().values.map { repo ->
                    async {
                        runCatching { repo.search(q, pageNumber, pageSize) }.getOrDefault(Page.empty())
                    }
                }

            mergePages(deferredPages.awaitAll())
        }

    /**
     * Helper function to merge contents from multiple Page objects, keeping the newest items,
     * and inheriting the pagination metadata from the source with the most elements.
     */
    private fun mergePages(pages: List<Page<T>>): Page<T> {
        if (pages.isEmpty()) return Page.empty()

        val allContentLists = pages.map { it.content }
        val idMaps = allContentLists.map { list -> list.associateBy { getId(it) } }
        val allKeys = idMaps.flatMap { it.keys }.toSet()

        val mergedContent = mutableListOf<T>()
        for (key in allKeys) {
            if (key == null) continue
            val candidates = idMaps.map { it[key] }
            val selectedItem = selectNewest(candidates)
            selectedItem?.let { mergedContent.add(it) }
        }

        val nullIdItems = allContentLists.flatten().filter { getId(it) == null }
        val finalContent = mergedContent + nullIdItems

        // Pick the pagination metadata from the repository that reports the largest dataset
        val basePage = pages.maxByOrNull { it.totalElements } ?: Page.empty()

        return basePage.copy(
            content = finalContent,
            numberOfElements = finalContent.size.toLong(),
            empty = finalContent.isEmpty(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getAll(): Flow<List<T>> =
        if (getRepos().isEmpty()) {
            flowOf(emptyList())
        } else {
            val flows = getRepos().values.map { it.getAll() as Flow<Any?> }
            combine(flows) { array ->
                val allLists = array.toList() as List<List<T>>
                val idMaps = allLists.map { list -> list.associateBy { getId(it) } }
                val allKeys = idMaps.flatMap { it.keys }.toSet()

                val merged = mutableListOf<T>()
                for (key in allKeys) {
                    if (key == null) continue
                    val candidates = idMaps.map { it[key] }
                    val selectedItem = selectNewest(candidates)
                    selectedItem?.let { merged.add(it) }
                }

                val nullIdItems = allLists.flatten().filter { getId(it) == null }
                merged + nullIdItems
            }
        }

    override suspend fun delete(id: Id): Result<Unit> =
        runCatching {
            for (repo in getRepos().values) {
                repo.delete(id).getOrThrow()
            }
        }

    override suspend fun deleteAll(ids: List<Id>): Result<Unit> =
        runCatching {
            for (repo in getRepos().values) {
                repo.deleteAll(ids).getOrThrow()
            }
        }

    override suspend fun fetchAll(): Result<Unit> =
        runCatching {
            val localRepo = crudRepositories[DataSource.Local]
            val remoteRepos = crudRepositories.filterKeys { it != DataSource.Local }.values
            if (localRepo != null) {
                for (remoteRepo in remoteRepos) {
                    val remoteItems = remoteRepo.getAll().first()
                    if (remoteItems.isNotEmpty()) {
                        localRepo.insertAll(remoteItems).getOrThrow()
                    }
                }
            }
        }

    override suspend fun uploadAll(): Result<Unit> =
        runCatching {
            val localRepo = crudRepositories[DataSource.Local]
            val remoteRepos = crudRepositories.filterKeys { it != DataSource.Local }.values
            if (localRepo != null) {
                val localItems = localRepo.getAll().first()
                if (localItems.isNotEmpty()) {
                    for (remoteRepo in remoteRepos) {
                        remoteRepo.insertAll(localItems).getOrThrow()
                    }
                }
            }
        }
}
