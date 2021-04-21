package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.GroupDao
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import org.springframework.stereotype.Component

@Component
class GroupQuery(private val groupDao: GroupDao): Query {
    suspend fun groups(gids: List<ID>?, graphQLAuthContext: GraphQLAuthContext): List<GraphQLUser.Group> {
        return when {
            gids != null -> gids.map { groupDao.getGroupById(it, graphQLAuthContext.mediqAuthToken) }.map { GraphQLUser.Group(it) }
            else -> throw IllegalArgumentException("invalid argument combination to groups")
        }
    }

    suspend fun allGroups(graphQLAuthContext: GraphQLAuthContext): List<GraphQLUser.Group> {
        return groupDao
            .getAllGroups(graphQLAuthContext.mediqAuthToken)
            .map { GraphQLUser.Group(it) }
    }
}