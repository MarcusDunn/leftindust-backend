package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID

@GraphQLName("DoctorReferenceInput")
data class GraphQLDoctorReferenceInput(
    val did: ID
)