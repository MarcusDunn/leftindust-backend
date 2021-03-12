package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("NameInput")
data class GraphQLNameInput(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
)
