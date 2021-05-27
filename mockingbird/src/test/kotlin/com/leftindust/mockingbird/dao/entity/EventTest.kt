package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import integration.util.EntityStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class EventTest {

    @Test
    fun update() {
        val eventID = UUID.randomUUID()
        val event = EntityStore.event("EventTest.update")
            .apply { id = eventID }
        val result = event.update(
            GraphQLEventEditInput(
                eid = GraphQLEvent.ID(eventID),
                description = OptionalInput.Defined("new fancy description"),
                allDay = true,
                end = OptionalInput.Defined(null)
            ),
            emptySet(),
            emptySet()
        )
        assertEquals("new fancy description", result.description)
        assertEquals(true, result.allDay)
        assertEquals(event.startTime, result.startTime)
        assertEquals(null, result.endTime)
    }
}