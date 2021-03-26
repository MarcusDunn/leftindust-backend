package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.GraphQLAddress
import com.leftindust.mockingbird.graphql.types.GraphQLAddressType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
class Address(
    @Enumerated(EnumType.STRING)
    var type: GraphQLAddressType,
    @Column(name = "address", nullable = false)
    var address: String, //todo validation
    @Column(name = "postal_code", nullable = false)
    var postalCode: String, //todo validation
) : AbstractJpaPersistable<Long>() {
    constructor(graphQLAddress: GraphQLAddress) : this(
        graphQLAddress.addressType,
        graphQLAddress.address,
        graphQLAddress.postalCode
    )
}
