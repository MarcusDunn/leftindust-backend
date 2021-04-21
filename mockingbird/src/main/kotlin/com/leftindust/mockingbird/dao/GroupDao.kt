package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput

interface GroupDao {
    suspend fun addGroup(group: GraphQLGroupInput, requester: MediqToken): MediqGroup
    suspend fun getAllGroups(requester: MediqToken): Collection<MediqGroup>
    suspend fun getGroupById(gid: ID, requester: MediqToken): MediqGroup
}
