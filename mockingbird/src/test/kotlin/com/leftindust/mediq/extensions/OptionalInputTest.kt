package com.leftindust.mediq.extensions

import com.expediagroup.graphql.execution.OptionalInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OptionalInputTest {
    @Test
    internal fun testGetOrDefaultOnUndefined() {
        val actual = OptionalInput.Undefined.getOrDefault(50)
        val expected = 50
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetOrDefaultOnDefined() {
        val actual = OptionalInput.Defined(10).getOrDefault(50)
        val expected = 10
        assertEquals(expected, actual)
    }

    @Test
    internal fun testGetOrDefaultOnDefinedNull() {
        val actual = OptionalInput.Defined(null).getOrDefault(50)
        val expected = 50
        assertEquals(expected, actual)
    }
}