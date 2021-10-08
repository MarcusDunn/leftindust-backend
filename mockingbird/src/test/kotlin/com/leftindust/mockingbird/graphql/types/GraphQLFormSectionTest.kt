package com.leftindust.mockingbird.graphql.types

import integration.util.EntityStore
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GraphQLFormSectionTest {
    @Test
    internal fun `create GraphQLFormSection from FormSection Entity`() {
        val section =
            EntityStore.form("GraphQLFormSectionTest.create GraphQLFormSection from FormSection Entity").sections.first()
        val gqlSection = GraphQLFormSection(section, mockk())
        assertEquals(section.name, gqlSection.name)
        assertEquals(section.fields.size, gqlSection.fields.size)
        assertEquals(section.fields.first().dataType, gqlSection.fields.first().dataType)
        assertEquals(section.number, gqlSection.number)
    }
}