package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class EventQuery(private val eventDao: EventDao) : Query {
    suspend fun events(range: GraphQLRangeInput?, graphQLAuthContext: GraphQLAuthContext): List<GraphQLEvent> {
        return when {
            range != null -> eventDao.getMany(range, graphQLAuthContext.mediqAuthToken).getOrThrow()
            else -> throw IllegalArgumentException("invalid argument combination to events")
        }.map { GraphQLEvent(it, it.id!!, graphQLAuthContext) }
    }
}