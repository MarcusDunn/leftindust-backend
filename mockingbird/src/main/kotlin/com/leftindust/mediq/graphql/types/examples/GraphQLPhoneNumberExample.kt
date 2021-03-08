package com.leftindust.mediq.graphql.types.examples

import com.leftindust.mediq.graphql.types.GraphQLPhoneType

data class GraphQLPhoneNumberExample(
    val number: LongFilter? = null,
    val type: GraphQLPhoneType? = null
)