package com.hoang.auth.data.repository.profile

import com.google.protobuf.MessageLite
import com.hoang.auth.data.remote.retrofit.ProfileProtoApiService
import com.hoang.auth.domain.entity.Profile
import com.hoang.auth.domain.entity.UserRole
import com.hoang.auth.domain.repository.ProfileRepository
import com.hoang.common.data.repository.BaseProtoApiCrudRepositoryImpl
import com.hoang.common.domain.entity.Page
import com.hoang.moneycare.core.auth.presentation.proto.ProfileProto
import com.hoang.moneycare.core.auth.presentation.proto.ProfileResponseProto
import com.hoang.moneycare.core.auth.presentation.proto.SaveAllProfileRequestProto
import com.hoang.moneycare.core.auth.presentation.proto.UserRoleProto
import com.hoang.moneycare.core.auth.presentation.proto.profileProto
import com.hoang.moneycare.core.auth.presentation.proto.saveAllProfileRequestProto
import com.hoang.moneycare.core.common.presentation.proto.idsProto

class ProfileProtoRepositoryImpl(
    apiService: ProfileProtoApiService? = null,
) : BaseProtoApiCrudRepositoryImpl<Profile, String, ProfileProto>(apiService),
    ProfileRepository {
    override fun toRemote(data: Profile): ProfileProto =
        profileProto {
            data.id?.let { id = it }
            email = data.email
            displayName = data.displayName
            data.token?.let { token = it }
            role =
                when (data.role) {
                    UserRole.USER -> UserRoleProto.USER
                    UserRole.ADMIN -> UserRoleProto.ADMIN
                    UserRole.MODERATOR -> UserRoleProto.MODERATOR
                }
            data.avatarUrl?.let { avatarUrl = it }
            createdAt = data.createdAt
            updatedAt = data.updatedAt
        }

    override fun toDomain(data: ProfileProto): Profile =
        Profile(
            id = data.id.ifEmpty { null },
            email = data.email,
            displayName = data.displayName,
            token = data.token.ifEmpty { null },
            role =
                when (data.role) {
                    UserRoleProto.USER -> UserRole.USER
                    UserRoleProto.ADMIN -> UserRole.ADMIN
                    UserRoleProto.MODERATOR -> UserRole.MODERATOR
                    UserRoleProto.UNRECOGNIZED -> UserRole.USER
                },
            avatarUrl = data.avatarUrl.ifEmpty { null },
            createdAt = data.createdAt,
            userId = data.id,
            updatedAt = data.updatedAt,
        )

    override fun toId(id: String): String = id

    override fun toBatchRemote(remotes: List<ProfileProto>): MessageLite =
        saveAllProfileRequestProto {
            profile.addAll(remotes)
        }

    override fun toIdsMessage(ids: List<String>): MessageLite =
        idsProto {
            this.ids.addAll(ids)
        }

    override fun toDomainList(remoteResponse: Any?): List<Profile> =
        when (remoteResponse) {
            is ProfileResponseProto -> remoteResponse.userProfilesList.map { toDomain(it) }
            is SaveAllProfileRequestProto -> remoteResponse.profileList.map { toDomain(it) }
            else -> super.toDomainList(remoteResponse)
        }

    override fun toDomainPage(remoteResponse: Any?): Page<Profile> {
        // Cast to your specific generated Protobuf Page response class
        val protoPage = remoteResponse as? ProfileResponseProto ?: return Page.empty()

        return Page(
            content = protoPage.userProfilesList.map { toDomain(it) },
            totalElements = protoPage.totalElements,
            totalPages = protoPage.totalPages,
            number = protoPage.pageNumber,
            size = protoPage.pageSize,
            numberOfElements = protoPage.totalElements,
            first = protoPage.first,
            last = protoPage.last,
            empty = protoPage.empty,
        )
    }
}
