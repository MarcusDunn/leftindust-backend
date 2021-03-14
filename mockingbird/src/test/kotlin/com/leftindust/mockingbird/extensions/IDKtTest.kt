package com.leftindust.mockingbird.extensions

import com.expediagroup.graphql.scalars.ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class IDKtTest {

    @Test
    fun `gqlID with ints`() {
        assertEquals(ID("10"), gqlID(10))
    }

    @Test
    fun `testGqlID with longs`() {
        assertEquals(ID("10"), gqlID(10L))
    }

    @Test
    fun `toInt with invalid int`() {
        assertThrows(NumberFormatException::class.java) {
            ID("twenty").toInt()
        }
    }

    @Test
    fun `toInt with valid int`() {
        assertEquals(20, ID("20").toInt())
    }

    @Test
    fun `toLong with valid long`() {
        assertEquals(20L, ID("20").toLong())

    }

    @Test
    fun `toLong with invalid long`() {
        assertThrows(NumberFormatException::class.java) {
            ID("twenty").toLong()
        }
    }
}