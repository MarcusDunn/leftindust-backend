package com.leftindust.mockingbird.graphql.types.input

import com.leftindust.mockingbird.graphql.types.GraphQLTime
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

data class GraphQLEventInput(
    val title: String,
    val description: String?,
    val start: GraphQLTimeInput,
    val end: GraphQLTimeInput,
)
