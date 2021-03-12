package com.leftindust.mockingbird.graphql.queries

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class HealthCheckTest(
    @Autowired private val healthCheck: HealthCheck
) {

    @Test
    fun mockingbirdIsAlive() {
        assertEquals(true, healthCheck.mockingbirdIsAlive())
    }
}