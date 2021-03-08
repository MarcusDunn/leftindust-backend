package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class HealthCheck: Query {
    fun mockingbirdIsAlive() = true
}