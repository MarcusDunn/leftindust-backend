package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode

data class GraphQLVisitInput(
    val timeBooked: GraphQLTime,
    val timeOfVisit: GraphQLTime,
    val title: String? = null,
    val description: String? = null,
    @GraphQLDescription("the foundation code")
    val foundationIcdCode: FoundationIcdCode,
    val doctorId: ID,
    val patientId: ID,
)
