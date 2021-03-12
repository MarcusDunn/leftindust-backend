package com.leftindust.mockingbird.graphql.types

data class GraphQLPhoneNumber(val number: Long, val type: GraphQLPhoneType) {
    constructor(stringyPhoneNumber: String, type: GraphQLPhoneType) : this(
        number = stringyPhoneNumber.replace(Regex("[^0-9]"), "").toLong(),
        type = type
    )
}