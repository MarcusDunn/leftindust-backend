package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import org.springframework.stereotype.Component

@Component
class EventMutation(private val eventDao: EventDao) : Mutation {
    fun addEvent(event: GraphQLEventInput, graphQLAuthContext: GraphQLAuthContext): GraphQLEvent {
        return eventDao
            .addEvent(event, graphQLAuthContext)
            .getOrThrow()
            .let { GraphQLEvent(it, it.id!!) }
    }
}