package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput

interface PermissionDao {
    suspend fun addUserPermission(
        uid: String,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList

    suspend fun addGroupPermission(
        gid: ID,
        permission: GraphQLPermissionInput,
        requester: MediqToken
    ): AccessControlList
}