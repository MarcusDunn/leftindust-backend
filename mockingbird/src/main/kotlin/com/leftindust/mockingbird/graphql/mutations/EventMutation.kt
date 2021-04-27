package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecurrenceEditSettings
import org.springframework.stereotype.Component

@Component
class EventMutation(private val eventDao: EventDao) : Mutation {
    suspend fun addEvent(event: GraphQLEventInput, graphQLAuthContext: GraphQLAuthContext): GraphQLEvent {
        return eventDao
            .addEvent(event, graphQLAuthContext.mediqAuthToken)
            .let {
                GraphQLEvent(
                    event = it,
                    authContext = graphQLAuthContext
                )
            }
    }

    @GraphQLDescription("edits the event referenced by eid")
    suspend fun editEvent(
        event: GraphQLEventEditInput,
        graphQLAuthContext: GraphQLAuthContext,
    ): GraphQLEvent {
        return eventDao
            .editEvent(event, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLEvent(it, graphQLAuthContext) }
    }

    @GraphQLDescription("edits the event referenced by eid")
    suspend fun editRecurringEvent(
        event: GraphQLEventEditInput,
        graphQLAuthContext: GraphQLAuthContext,
        recurrenceSettings: GraphQLRecurrenceEditSettings
    ): GraphQLEvent {
        return eventDao
            .editRecurringEvent(event, graphQLAuthContext.mediqAuthToken, recurrenceSettings)
            .let { GraphQLEvent(it, graphQLAuthContext) }
    }
}
