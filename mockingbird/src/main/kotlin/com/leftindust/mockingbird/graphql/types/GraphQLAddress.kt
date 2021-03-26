package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.dao.entity.Address

data class GraphQLAddress(
    val addressType: GraphQLAddressType,
    val address: String,
    val postalCode: String,
) {
    constructor(address: Address) : this(address.type, address.address, address.postalCode)
}
