package com.leftindust.condor.graphql.queries

import com.leftindust.condor.graphql.types.CondorStatus
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
            CondorStatus(
                isAlive = true,
                connectedToDatabase = false,
            ),
            result
        )
    }
}