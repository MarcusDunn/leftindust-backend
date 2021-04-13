package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput

data class GraphQLEventInput(
    val title: String,
    val description: String? = null,
    @GraphQLDescription("UTC")
    val start: GraphQLTimeInput,
    @GraphQLDescription("UTC")
    val end: GraphQLTimeInput,
    @GraphQLDescription("defaults to false")
    val allDay: Boolean? = false,
    val doctors: List<ID>? = emptyList(),
    val patients: List<ID>? = emptyList(),
)