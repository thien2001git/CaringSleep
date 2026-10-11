package com.hoang.common.domain.entity

interface BaseIdEntity<T> : BaseEntity {
    val id: T?
}
