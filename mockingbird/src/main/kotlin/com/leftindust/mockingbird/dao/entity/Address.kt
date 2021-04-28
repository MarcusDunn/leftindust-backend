package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLAddressType
import com.leftindust.mockingbird.graphql.types.input.GraphQLAddressInput
import javax.persistence.*

@Entity(name = "address")
class Address(
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true)
    var type: GraphQLAddressType?,
    @Column(name = "address", nullable = false)
    var address: String, //todo validation
    @Column(name = "city", nullable = false)
    var city: String,
    @Embedded
    var countryState: CountryState,
    @Column(name = "postal_code", nullable = false)
    var postalCode: String, //todo validation
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLAddress: GraphQLAddressInput) : this(
        type = graphQLAddress.addressType,
        address = graphQLAddress.address,
        city = graphQLAddress.city,
        countryState = CountryState(
            country = graphQLAddress.country,
            province = graphQLAddress.province
        ),
        postalCode = graphQLAddress.postalCode,
    )
}
