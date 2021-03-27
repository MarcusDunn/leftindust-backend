package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName

@GraphQLName("NameInput")
data class GraphQLNameInput(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
)