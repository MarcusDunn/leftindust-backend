package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.graphql.types.GraphQLPhone
import com.leftindust.mockingbird.graphql.types.GraphQLTime

@GraphQLName("DoctorInput")
data class GraphQLDoctorInput(
    val did: Int,
    val name: GraphQLNameInput,
    val phones: List<GraphQLPhone>? = null,
    val dateOfBirth: GraphQLTime,
    val email: String,
)

