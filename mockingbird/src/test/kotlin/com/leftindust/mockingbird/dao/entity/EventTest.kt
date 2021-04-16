package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import integration.util.EntityStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventTest {

    @Test
    fun update() {
        val event = EntityStore.event("EventTest.update")
            .apply { id = 1000 }
        val result = event.update(
            GraphQLEventEditInput(
                eid = gqlID(1000),
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