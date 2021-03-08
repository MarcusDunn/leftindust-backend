package com.leftindust.condor.graphql.queries

import com.leftindust.condor.graphql.queries.HealthCheck
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class HealthCheckTest(
    @Autowired private val healthCheck: HealthCheck
) {
    @Test
    internal fun `condor is alive`() {
        assertEquals(true, healthCheck.condorIsAlive())
    }
}