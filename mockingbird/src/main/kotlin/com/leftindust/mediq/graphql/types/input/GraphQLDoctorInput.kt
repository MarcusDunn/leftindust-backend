package com.leftindust.mediq.graphql.types.input

import com.expediagroup.graphql.annotations.GraphQLName
import com.leftindust.mediq.graphql.types.GraphQLPhoneNumber
import com.leftindust.mediq.graphql.types.GraphQLTime

@GraphQLName("DoctorInput")
data class GraphQLDoctorInput(
    val did: Int,
    val name: GraphQLNameInput,
    val phoneNumbers: List<GraphQLPhoneNumber>? = null,
    val dateOfBirth: GraphQLTime,
    val email: String,
)

