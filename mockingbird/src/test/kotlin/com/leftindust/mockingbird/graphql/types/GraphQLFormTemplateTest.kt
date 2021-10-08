package com.leftindust.mockingbird.graphql.types

import integration.util.EntityStore
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GraphQLFormTemplateTest {
    @Test
    internal fun `create GraphQLFormTemplate from Form Entity`() {
        val entityForm = EntityStore.form("FormTemplateTest.create GraphQLFormTemplate from Form Entity")
        val gqlFormTemplate = GraphQLFormTemplate(entityForm, mockk())
        assertEquals(entityForm.name, gqlFormTemplate.name)
        assertEquals(entityForm.sections.size, gqlFormTemplate.sections.size)
        assertEquals(entityForm.sections.first().name, gqlFormTemplate.sections.first().name)
        assertEquals(
            entityForm.sections.first().fields.first().dataType,
            gqlFormTemplate.sections.first().fields.first().dataType
        )
    }
}