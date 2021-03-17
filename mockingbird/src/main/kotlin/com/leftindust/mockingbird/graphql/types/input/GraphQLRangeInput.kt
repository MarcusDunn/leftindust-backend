package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.GraphQLKotlinException

@GraphQLName("RangeInput")
data class GraphQLRangeInput(
    val from: Int,
    val to: Int,
) {
    @GraphQLIgnore
    @Throws(GraphQLKotlinException::class)
    fun toIntRange(): IntRange {
        return from..to
    }
}