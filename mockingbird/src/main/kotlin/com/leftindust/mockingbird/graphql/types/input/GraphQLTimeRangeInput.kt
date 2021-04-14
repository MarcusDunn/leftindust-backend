package com.leftindust.mockingbird.graphql.types.input

import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

data class GraphQLTimeRangeInput(
    val start: GraphQLTimeInput,
    val end: GraphQLTimeInput,
) {
    init {
        if (start.before(end)) {
            // ok
        } else {
            throw IllegalArgumentException("start must be before end")
        }
    }
}
