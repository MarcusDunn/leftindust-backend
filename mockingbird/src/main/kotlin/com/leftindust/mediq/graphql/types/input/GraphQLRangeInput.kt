package com.leftindust.mediq.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.GraphQLKotlinException

@GraphQLName("RangeInput")
data class GraphQLRangeInput(
    val from: Int? = null,
    val to: Int? = null,
) {
    @GraphQLIgnore
    @Throws(GraphQLKotlinException::class)
    fun validateAndGetOrDefault(fromDefault: Int = 0, toDefault: Int = 20): IntRange {
        val nnFrom = from ?: fromDefault
        val nnTo = to ?: nnFrom + toDefault

        if (nnFrom > nnTo || nnFrom < 0 || nnTo <= 0) {
            throw GraphQLKotlinException(
                "invalid to and from(from: $nnFrom, to: $nnTo)",
                IllegalArgumentException()
            )
        }
        return nnFrom..nnTo
    }
}