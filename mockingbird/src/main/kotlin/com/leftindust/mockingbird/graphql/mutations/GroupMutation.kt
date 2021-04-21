package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.GroupDao
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import org.springframework.stereotype.Component

@Component
class GroupMutation(private val groupDao: GroupDao) : Mutation {
    suspend fun addGroup(group: GraphQLGroupInput, graphQLAuthContext: GraphQLAuthContext): GraphQLUser.Group {
        return groupDao
            .addGroup(group, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLUser.Group(it) }
    }
}