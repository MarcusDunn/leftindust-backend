package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventQueryTest {
    private val eventDao = mockk<EventDao>()

    @Test
    fun events() {
        val listOfEvent = (0 until 20).map {
            mockk<Event>(relaxed = true) {
                every { id } returns it.toLong()
            }
        }

        coEvery { eventDao.getMany(any(), any()) } returns Success(listOfEvent)

        val eventQuery = EventQuery(eventDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking { eventQuery.events(GraphQLRangeInput(0, 20), graphQLAuthContext) }

        assertEquals(listOfEvent.map { GraphQLEvent(it, it.id!!, graphQLAuthContext) }, result)
    }
}