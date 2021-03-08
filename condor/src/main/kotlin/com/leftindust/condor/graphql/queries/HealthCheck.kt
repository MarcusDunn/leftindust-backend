package com.leftindust.condor.graphql.queries

import com.expediagroup.graphql.types.operations.Query
import org.springframework.stereotype.Service

@Service
class HealthCheck : Query {

    fun condorIsAlive(): Boolean {
        return true
    }
}