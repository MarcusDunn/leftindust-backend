package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput

interface GroupDao {
    suspend fun addGroup(group: GraphQLGroupInput, requester: MediqToken): MediqGroup
    suspend fun getGroupById(gid: GraphQLUser.Group.ID, requester: MediqToken): MediqGroup
    suspend fun getRange(range: GraphQLRangeInput, requester: MediqToken): Collection<MediqGroup>
}
