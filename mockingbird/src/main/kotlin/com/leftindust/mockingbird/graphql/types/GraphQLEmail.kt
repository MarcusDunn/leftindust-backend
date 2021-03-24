package com.leftindust.mockingbird.graphql.types

data class GraphQLEmail(
    val type: GraphQLEmailType,
    val email: String,
)
