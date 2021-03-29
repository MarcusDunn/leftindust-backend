package com.leftindust.condor.graphql.queries

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HealthQueryTest {

    @Test
    // test has an issue mocking datasource. see: https://github.com/mockk/mockk/issues/280
    fun condorIsAlive() {
        val healthQuery = HealthQuery()
        val result = healthQuery.condorIsAlive(mockk())

        assertEquals(
            HealthQuery.CondorStatus(
                isAlive = true,
                connectedToDatabase = false,
            ),
            result
        )
    }
}