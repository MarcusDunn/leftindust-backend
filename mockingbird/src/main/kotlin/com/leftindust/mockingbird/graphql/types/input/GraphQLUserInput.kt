package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLJsonObject

@GraphQLName("InputUser")
data class GraphQLUserInput(
    val uid: String,
    val group_id: ID? = null,
)