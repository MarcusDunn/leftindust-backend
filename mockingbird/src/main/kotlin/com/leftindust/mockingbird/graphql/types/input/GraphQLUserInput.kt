package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLJsonObject

@GraphQLName("InputUser")
data class GraphQLUserInput(
    val uid: String,
    val group_id: ID? = null,
    val settings_version: Int,
    val settings: GraphQLJsonObject,
)
