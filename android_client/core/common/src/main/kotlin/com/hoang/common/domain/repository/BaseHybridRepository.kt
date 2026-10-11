package com.hoang.common.domain.repository

interface BaseHybridRepository<T, Id> :
    BaseCrudRepository<T, Id>,
    BaseRemoteRepository
