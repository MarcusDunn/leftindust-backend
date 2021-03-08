package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("NameInput")
data class GraphQLNameInput(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
)
