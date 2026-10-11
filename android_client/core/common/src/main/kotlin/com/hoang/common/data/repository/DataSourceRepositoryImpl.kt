package com.hoang.common.data.repository

import com.hoang.common.data.local.pref.DataSourcePref
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.repository.DataSourceRepository

class DataSourceRepositoryImpl(
    pref: DataSourcePref,
) : BaseKvRepositoryImpl<Set<DataSource>, String>(pref),
    DataSourceRepository {
    override fun toDomain(data: String): Set<DataSource> = gson.fromJson(data, Array<DataSource>::class.java).toSet()

    override fun toEntity(data: Set<DataSource>): String = gson.toJson(data)
}
