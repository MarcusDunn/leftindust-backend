package com.leftindust.mockingbird.graphql.types

import integration.util.EntityStore
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GraphQlFormFieldTest {
    @Test
    internal fun `create GraphQLFormFieldTest from FormField Entity`() {
        val formField = EntityStore.formField("GraphQlFormFieldTest.create GraphQLFormFieldTest from FormField Entity")
        val gqlFormField = GraphQlFormField(formField, mockk())
        assertEquals(formField.dataType, gqlFormField.dataType)
        assertEquals(formField.number, gqlFormField.number)
        assertEquals(formField.multiSelectPossibilities, gqlFormField.multiSelectPossibilities)
    }
}