package com.leftindust.mockingbird.graphql.types.examples

import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType

data class GraphQLPhoneNumberExample(
    val number: LongFilter? = null,
    val type: GraphQLPhoneType? = null
)