package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
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
        val result = runBlocking {
            eventMutation.addEvent(
                EntityStore.graphQLEventInput("EventMutationTest.addEvent"),
                graphQLAuthContext
            )
        }

        assertEquals(GraphQLEvent(mockkEvent, mockkEvent.id!!), result)
    }
}