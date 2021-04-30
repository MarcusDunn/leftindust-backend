package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID

data class GraphQLClinicInput(
    val name: String,
    val address: GraphQLAddressInput,
    @GraphQLDescription("defaults to empty list")
    val doctors: List<ID>? = null
)
