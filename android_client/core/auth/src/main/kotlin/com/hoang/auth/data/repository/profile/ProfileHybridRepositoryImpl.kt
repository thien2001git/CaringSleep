package com.hoang.auth.data.repository.profile

import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.repository.ProfileRepository
import com.hoang.common.data.repository.BaseHybridRepositoryImpl
import com.hoang.common.domain.entity.DataSource
import com.hoang.common.domain.repository.BaseCrudRepository
import com.hoang.common.domain.repository.DataSourceRepository

class ProfileHybridRepositoryImpl(
    crudRepositories: Map<DataSource, BaseCrudRepository<Profile, String>>,
    dataSourceRepository: DataSourceRepository,
) : BaseHybridRepositoryImpl<Profile, String>(crudRepositories, dataSourceRepository),
    ProfileRepository {
    override fun getId(data: Profile): String? = data.id

    override fun isLocalNewer(
        local: Profile?,
        remote: Profile?,
    ): Boolean =
        when {
            local == null -> false
            remote == null -> true
            else -> local.updatedAt >= remote.updatedAt
        }
}
