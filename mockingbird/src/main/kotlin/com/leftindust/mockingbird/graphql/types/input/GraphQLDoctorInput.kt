package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.GraphQLAddress
import com.leftindust.mockingbird.graphql.types.GraphQLEmail
import com.leftindust.mockingbird.graphql.types.GraphQLPhone
import com.leftindust.mockingbird.graphql.types.GraphQLTime

@GraphQLName("DoctorInput")
data class GraphQLDoctorInput(
    val did: ID,
    val user: GraphQLUserInput? = null,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val phones: List<GraphQLPhone>,
    val title: String? = null,
    val dateOfBirth: GraphQLTime,
    val addresses: List<GraphQLAddress> = emptyList(),
    val emails: List<GraphQLEmail> = emptyList(),
    val patients: List<ID> = emptyList(),
)