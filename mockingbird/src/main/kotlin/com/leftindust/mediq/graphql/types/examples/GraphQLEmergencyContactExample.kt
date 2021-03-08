package com.leftindust.mediq.graphql.types.examples

import com.leftindust.mediq.dao.entity.enums.Relationship
import com.leftindust.mediq.graphql.types.examples.GraphQLPersonExample

data class GraphQLEmergencyContactExample(
    val personalInformation: GraphQLPersonExample? = null,
    val relationship: Relationship? = null,
)