package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID

data class GraphQLClinicInput(
    val name: String,
    val address: GraphQLAddressInput,
    @GraphQLDescription("defaults to empty list")
    val doctors: List<ID>? = null
)

data class GraphQLClinicEditInput(
    val id: ID,
    val name: String? = null,
    val address: GraphQLAddressEditInput? = null,
    @GraphQLDescription("passing null will not update, to clear: pass an empty list")
    val doctors: List<ID>? = null
)