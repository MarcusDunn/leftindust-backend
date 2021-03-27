package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID

@GraphQLName("DoctorReferenceInput")
data class GraphQLDoctorReferenceInput(
    val did: ID
)