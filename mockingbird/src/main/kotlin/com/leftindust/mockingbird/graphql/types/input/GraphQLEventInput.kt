package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

data class GraphQLEventInput(
    val title: String,
    val description: String? = null,
    val start: GraphQLTimeInput,
    val end: GraphQLTimeInput,
    val doctor: ID? = null,
)
