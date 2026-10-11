package com.hoang.common.domain.entity

import com.google.gson.annotations.SerializedName // Or use @SerialName for kotlinx.serialization

/**
 * A generic class representing a paginated response, modeled after Spring Data's Page<T>.
 */
data class Page<T>(
    @SerializedName("content")
    val content: List<T> = emptyList(),
    @SerializedName("totalElements")
    val totalElements: Long = 0,
    @SerializedName("totalPages")
    val totalPages: Int = 0,
    @SerializedName("number")
    val number: Int = 0,
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("numberOfElements")
    val numberOfElements: Long = 0,
    @SerializedName("first")
    val first: Boolean = true,
    @SerializedName("last")
    val last: Boolean = true,
    @SerializedName("empty")
    val empty: Boolean = true,
) : BaseEntity {
    /**
     * Transforms a Page<T> into a Page<R> by applying a mapping function to each item in the content.
     * Extremely useful for Clean Architecture when mapping Data/Network models to Domain models.
     */
    fun <R> map(transform: (T) -> R): Page<R> =
        Page(
            content = this.content.map(transform),
            totalElements = this.totalElements,
            totalPages = this.totalPages,
            number = this.number,
            size = this.size,
            numberOfElements = this.numberOfElements,
            first = this.first,
            last = this.last,
            empty = this.empty,
        )

    companion object {
        /**
         * Returns an empty Page instance.
         */
        fun <T> empty(): Page<T> = Page()
    }
}
