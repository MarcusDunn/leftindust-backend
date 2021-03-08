package com.leftindust.caper.graphql.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.types.operations.Query
import org.springframework.stereotype.Component

@Component
class Health : Query {
    @GraphQLDescription("always returns true")
    fun caperIsAlive(): Boolean {
        return true
    }
}