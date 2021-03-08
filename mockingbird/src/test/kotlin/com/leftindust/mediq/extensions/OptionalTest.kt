package com.leftindust.mediq.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class OptionalTest {
    @Test
    fun `test UnwrapOrNull with empty`() {
        val result = Optional.empty<Int>().unwrapOrNull()
        assert(result == null)
    }

    @Test
    fun `test UnwrapOrNull with present`() {
        val result = Optional.of(10).unwrapOrNull()
        assertEquals(result, 10)
    }

    @Test
    fun `test UnwrapAsSuccessOrNull with empty`() {
        val result = Optional.empty<Int>().unwrapAsSuccessOrNull()
        assert(result == null)
    }

    @Test
    fun `test UnwrapAsSuccessOrNull with present`() {
        val result = Optional.of(10).unwrapAsSuccessOrNull()
        assertEquals(result, Success(10))
    }
}