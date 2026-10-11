package com.hoang.common.data.repository

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hoang.common.domain.entity.BaseIdEntity
import com.hoang.common.domain.entity.Page
import com.hoang.common.domain.provider.UserIdProvider
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.utils.AppLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.math.ceil

abstract class BaseFireBaseCrudRepositoryImpl<T : BaseIdEntity<Id>, Id>(
    private val collectionRef: CollectionReference,
    userIdProvider: UserIdProvider,
    private val clazz: Class<T>,
) : BaseCrudRepository<T, Id> {
    abstract fun getId(id: Id): String

    abstract fun getId(data: T): String?

    // Abstract property so child repositories can define which field to search on
    abstract val searchFields: List<String>

    private val firestore: FirebaseFirestore
        get() = collectionRef.firestore

    val currentUserId: String = userIdProvider.getCurrentUserId()
    open val userIdField: String = "userId"

    override suspend fun insert(data: T): Result<Unit> =
        runCatching {
            val id = getId(data)
            val docRef =
                if (id != null) {
                    collectionRef.document(id)
                } else {
                    collectionRef.document()
                }
            docRef.set(data).await()
            Unit
        }.onSuccess {
            AppLog.d("Successfully inserted item of type ${clazz.simpleName}")
        }.onFailure {
            AppLog.e("Failed to insert item of type ${clazz.simpleName}", it)
        }

    override suspend fun insertAll(data: List<T>): Result<Unit> =
        runCatching {
            if (data.isEmpty()) return@runCatching

            data.chunked(500).forEach { chunk ->
                val batch = firestore.batch()
                for (item in chunk) {
                    val id = getId(item)
                    val docRef =
                        if (id != null) {
                            collectionRef.document(id)
                        } else {
                            collectionRef.document()
                        }
                    batch.set(docRef, item)
                }
                batch.commit().await()
            }
        }.onSuccess {
            AppLog.d("Successfully inserted ${data.size} items of type${clazz.simpleName}")
        }.onFailure {
            AppLog.e("Failed to insert ${data.size} items of type${clazz.simpleName}", it)
        }

    override fun getById(id: Id): Flow<T?> =
        callbackFlow {
            val docRef = collectionRef.document(getId(id))
            val registration: ListenerRegistration =
                docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        AppLog.e("getById error for id: $id in ${clazz.simpleName}", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        // Verify the document belongs to the current user
                        val documentOwnerId = snapshot.getString(userIdField)

                        if (documentOwnerId == currentUserId) {
                            AppLog.d("getById onSnapshot for id: $id in ${clazz.simpleName}")
                            trySend(snapshot.toObject(clazz))
                        } else {
                            AppLog.e("Unauthorized access attempt for id: $id in ${clazz.simpleName}")
                            trySend(null) // Return null if it belongs to someone else
                        }
                    } else {
                        AppLog.d("getById document does not exist for id: $id in ${clazz.simpleName}")
                        trySend(null)
                    }
                }

            awaitClose { registration.remove() }
        }

    override suspend fun getPage(
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        runCatching {
            // 2. Create a base query filtered by the user ID instead of using the raw collectionRef
            val userQuery = collectionRef.whereEqualTo(userIdField, currentUserId)

            val countSnapshot = userQuery.count().get(AggregateSource.SERVER).await()
            val totalElements = countSnapshot.count

            val fetchLimit = (pageNumber + 1) * pageSize

            // 3. Apply the limit to the user-filtered query
            val dataSnapshot =
                userQuery
                    .limit(fetchLimit.toLong())
                    .get()
                    .await()

            val allDocs = dataSnapshot.documents
            val startIndex = pageNumber * pageSize

            val pageDocs =
                if (startIndex < allDocs.size) {
                    allDocs.subList(startIndex, minOf(startIndex + pageSize, allDocs.size))
                } else {
                    emptyList()
                }

            val content = pageDocs.mapNotNull { it.toObject(clazz) }
            val totalPages = ceil(totalElements.toDouble() / pageSize).toInt()

            Page(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
                number = pageNumber,
                size = pageSize,
                numberOfElements = content.size.toLong(),
                first = pageNumber == 0,
                last = pageNumber >= totalPages - 1 || totalPages == 0,
                empty = content.isEmpty(),
            )
        }.getOrElse {
            AppLog.e("Failed to get page in ${clazz.simpleName}", it)
            Page.empty()
        }

    override suspend fun search(
        q: String,
        pageNumber: Int,
        pageSize: Int,
    ): Page<T> =
        runCatching {
            // 4. Chain the user filter with your search filters
            val query =
                collectionRef
                    .whereEqualTo(userIdField, currentUserId)
                    .apply {
                        searchFields.forEach {
                            whereGreaterThanOrEqualTo(it, q)
                                .whereLessThanOrEqualTo(it, q + "\uf8ff")
                        }
                    }

            val countSnapshot = query.count().get(AggregateSource.SERVER).await()
            val totalElements = countSnapshot.count

            val fetchLimit = (pageNumber + 1) * pageSize

            val dataSnapshot =
                query
                    .limit(fetchLimit.toLong())
                    .get()
                    .await()

            val allDocs = dataSnapshot.documents
            val startIndex = pageNumber * pageSize

            val pageDocs =
                if (startIndex < allDocs.size) {
                    allDocs.subList(startIndex, minOf(startIndex + pageSize, allDocs.size))
                } else {
                    emptyList()
                }

            val content = pageDocs.mapNotNull { it.toObject(clazz) }
            val totalPages = ceil(totalElements.toDouble() / pageSize).toInt()

            Page(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
                number = pageNumber,
                size = pageSize,
                numberOfElements = content.size.toLong(),
                first = pageNumber == 0,
                last = pageNumber >= totalPages - 1 || totalPages == 0,
                empty = content.isEmpty(),
            )
        }.getOrElse {
            AppLog.e("Failed to search in ${clazz.simpleName}", it)
            Page.empty()
        }

    override suspend fun getAll(): Flow<List<T>> =
        callbackFlow {
            // 5. Apply the user filter to the real-time listener
            val userQuery = collectionRef.whereEqualTo(userIdField, currentUserId)

            val registration: ListenerRegistration =
                userQuery.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        AppLog.e("getAll error for type ${clazz.simpleName}", error)
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { it.toObject(clazz) }
                        AppLog.d("getAll onSnapshot: ${list.size} items of type ${clazz.simpleName} loaded")
                        trySend(list)
                    } else {
                        trySend(emptyList())
                    }
                }

            awaitClose { registration.remove() }
        }

    override suspend fun delete(id: Id): Result<Unit> =
        runCatching {
            collectionRef.document(getId(id)).delete().await()
            Unit
        }.onSuccess {
            AppLog.d("Successfully deleted item with id: $id in${clazz.simpleName}")
        }.onFailure {
            AppLog.e("Failed to delete item with id: $id in${clazz.simpleName}", it)
        }

    override suspend fun deleteAll(ids: List<Id>): Result<Unit> =
        runCatching {
            if (ids.isEmpty()) return@runCatching

            ids.chunked(500).forEach { chunk ->
                val batch = firestore.batch()
                for (id in chunk) {
                    val docRef = collectionRef.document(getId(id))
                    batch.delete(docRef)
                }
                batch.commit().await()
            }
            Unit
        }.onSuccess {
            AppLog.d("Successfully deleted ${ids.size} items in${clazz.simpleName}")
        }.onFailure {
            AppLog.e("Failed to delete ${ids.size} items in${clazz.simpleName}", it)
        }
}
