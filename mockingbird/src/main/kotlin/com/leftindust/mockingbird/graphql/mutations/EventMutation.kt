package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import org.springframework.stereotype.Component

@Component
class EventMutation(private val eventDao: EventDao) : Mutation {
    suspend fun addEvent(event: GraphQLEventInput, graphQLAuthContext: GraphQLAuthContext): GraphQLEvent {
        return eventDao
            .addEvent(event, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
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
        recurrenceSettings: GraphQLRecurrenceEditSettings? = null
    ): GraphQLEvent {
        return eventDao
            .editEvent(event, graphQLAuthContext.mediqAuthToken, recurrenceSettings)
            .let { GraphQLEvent(it, graphQLAuthContext) }
    }

    @GraphQLName("RecurrenceEditSettings")
    @GraphQLDescription(
        """the date range that the edits will effect the reoccurring event. This allows things such as editing a 
            single event of a reoccurring event or leaving the past events untouched but editing future ones"""
    )
    data class GraphQLRecurrenceEditSettings(
        @GraphQLDescription("the start of events that the edit should take place on")
        val editStart: GraphQLDateInput,
        @GraphQLDescription("the end of the events that the edit should take place on")
        val editEnd: GraphQLDateInput,
    )
}


