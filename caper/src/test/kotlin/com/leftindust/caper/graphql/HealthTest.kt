package com.leftindust.caper.graphql

import com.leftindust.caper.graphql.query.Health
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

internal class HealthTest {
    private val health = Health()

    @Test
    internal fun `is alive`() {
        assertEquals(true, health.isAlive())
    }
}