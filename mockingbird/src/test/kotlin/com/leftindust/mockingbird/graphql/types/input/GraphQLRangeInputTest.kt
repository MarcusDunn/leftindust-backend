package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GraphQLRangeInputTest {

    @Test
    fun `validateAndGetOrDefault with default args`() {
        val defaultInput = GraphQLRangeInput()

        val result = defaultInput.validateAndGetOrDefault()

        assertEquals(0..20, result)
    }

    @Test
    fun `validateAndGetOrDefault with non-default args`() {
        val defaultInput = GraphQLRangeInput(35, 50)

        val result = defaultInput.validateAndGetOrDefault()

        assertEquals(35..50, result)
    }

    @Test
    fun `validateAndGetOrDefault with invalid args`() {
        val defaultInput = GraphQLRangeInput(60, 50)

        assertThrows(GraphQLKotlinException::class.java) {
            defaultInput.validateAndGetOrDefault()
        }
    }

    @Test
    fun `validateAndGetOrDefault with invalid args on validate call`() {
        val defaultInput = GraphQLRangeInput()

        assertThrows(GraphQLKotlinException::class.java) {
            defaultInput.validateAndGetOrDefault(60, -10)
        }
    }
}