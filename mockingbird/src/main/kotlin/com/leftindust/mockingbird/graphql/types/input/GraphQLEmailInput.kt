package com.leftindust.mockingbird.graphql.types.input

import com.leftindust.mockingbird.graphql.types.GraphQLEmailType

data class GraphQLEmailInput(
    val type: GraphQLEmailType,
    val email: String,
)

