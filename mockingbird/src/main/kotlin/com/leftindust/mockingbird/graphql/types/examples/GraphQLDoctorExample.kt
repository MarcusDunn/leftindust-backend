package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.generator.scalars.ID

data class GraphQLDoctorExample(
    val did: ID? = null,
    val personalInformation: GraphQLPersonExample? = null,
    val title: String? = null,
    val dateOfBirth: GraphQLTimeExample? = null,
    val address: String? = null,
    val email: String? = null,
)