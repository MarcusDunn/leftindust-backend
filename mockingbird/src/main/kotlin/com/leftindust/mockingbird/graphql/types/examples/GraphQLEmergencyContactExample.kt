package com.leftindust.mockingbird.graphql.types.examples

import com.leftindust.mockingbird.dao.entity.enums.Relationship

data class GraphQLEmergencyContactExample(
    val personalInformation: GraphQLPersonExample? = null,
    val relationship: Relationship? = null,
)