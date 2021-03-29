package com.leftindust.mockingbird.graphql.types.input

import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.graphql.types.GraphQLPhone

data class GraphQLEmergencyContactInput(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val relationship: Relationship,
    val phones: List<GraphQLPhone>,
)
