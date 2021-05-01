package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.dao.entity.Phone

@GraphQLName("Phone")
data class GraphQLPhone(
    val number: Long,
    val type: GraphQLPhoneType
) {
    constructor(stringyPhoneNumber: String, type: GraphQLPhoneType) : this(
        number = stringyPhoneNumber.replace(Regex("[^0-9]"), "").toLong(),
        type = type
    )

    constructor(phone: Phone) : this(phone.number, phone.type)
}