package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.graphql.types.GraphQLAddressType
import com.leftindust.mockingbird.graphql.types.GraphQLCountry

@GraphQLName("Address")
data class GraphQLAddressInput(
    val addressType: GraphQLAddressType? = null,
    val address: String,
    val city: String,
    val country: GraphQLCountry,
    val province: String,
    val postalCode: String,
)