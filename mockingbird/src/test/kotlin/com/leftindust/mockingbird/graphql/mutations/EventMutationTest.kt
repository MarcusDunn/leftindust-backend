package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventMutationTest {
    private val eventDao = mockk<EventDao>()

    @Test
    fun addEvent() {
        val graphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }
        val mockkEvent = mockk<Event>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { eventDao.addEvent(any(), any()) } returns Success(mockkEvent)

        val eventMutation = EventMutation(eventDao)
        val event = EntityStore.graphQLEventInput("EventMutationTest.addEvent")
        val result = runBlocking {
            eventMutation.addEvent(
                event,
                graphQLAuthContext
            )
        }

        assertEquals(GraphQLEvent(mockkEvent, graphQLAuthContext), result)
    }

    @Test
    internal fun `edit event`() {
        val graphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val eventMutation = EventMutation(eventDao)

        val expected = mockk<Event>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { eventDao.editEvent(any(), any()) } returns expected

        val result = runBlocking {
            eventMutation.editEvent(
                GraphQLEventEditInput(
                    gqlID(1000),
                    description = OptionalInput.Defined("new descr")
                ), graphQLAuthContext
            )
        }

        assertEquals(GraphQLEvent(expected, graphQLAuthContext), result)
    }
}