package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import integration.util.EntityStore
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventMutationTest {
    private val eventDao = mockk<EventDao>()

    @AfterEach
    internal fun tearDown() {
        confirmVerified(eventDao)
    }

    @Test
    fun addEvent() {
        val mockkContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }
        val mockkEvent = mockk<Event>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { eventDao.addEvent(any(), any()) } returns mockkEvent

        val eventMutation = EventMutation(eventDao)
        val event = EntityStore.graphQLEventInput("EventMutationTest.addEvent")
        val result = runBlocking {
            eventMutation.addEvent(
                event,
                mockkContext
            )
        }

        coVerifyAll {
            mockkContext.mediqAuthToken
            mockkEvent.id
            mockkEvent.title
            mockkEvent.description
            mockkEvent.startTime
            mockkEvent.endTime
            mockkEvent.allDay
            mockkEvent.reoccurrence
            eventDao.addEvent(any(), any())
        }

        confirmVerified(mockkEvent, mockkContext)

        assertEquals(GraphQLEvent(mockkEvent, mockkContext), result)

    }

    @Test
    internal fun `edit event`() {
        val mockkContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val eventMutation = EventMutation(eventDao)

        val mockkEvent = mockk<Event>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { eventDao.editEvent(any(), any()) } returns mockkEvent

        val result = runBlocking {
            eventMutation.editEvent(
                GraphQLEventEditInput(
                    gqlID(1000),
                    description = OptionalInput.Defined("new descr")
                ), mockkContext
            )
        }


        coVerifyAll {
            mockkContext.mediqAuthToken
            mockkEvent.id
            mockkEvent.title
            mockkEvent.description
            mockkEvent.startTime
            mockkEvent.endTime
            mockkEvent.allDay
            mockkEvent.reoccurrence
            eventDao.editEvent(any(), any())
        }

        confirmVerified(mockkEvent, mockkContext)

        assertEquals(GraphQLEvent(mockkEvent, mockkContext), result)

    }

    @Test
    internal fun `edit event with recurrence`() {
        val editedEvent = mockk<Event>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { eventDao.editRecurringEvent(any(), any(), any()) } returns editedEvent

        val eventMutation = EventMutation(eventDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }


        val result = runBlocking {
            eventMutation.editRecurringEvent(
                mockk(relaxed = true),
                graphQLAuthContext,
                mockk()
            )
        }


        coVerifyAll {
            eventDao.editRecurringEvent(any(), any(), any())
            graphQLAuthContext.mediqAuthToken
            editedEvent.id
            editedEvent.reoccurrence
            editedEvent.title
            editedEvent.description
            editedEvent.startTime
            editedEvent.endTime
            editedEvent.allDay
        }

        confirmVerified(editedEvent, graphQLAuthContext)

        assertEquals(GraphQLEvent(editedEvent, graphQLAuthContext), result)
    }
}