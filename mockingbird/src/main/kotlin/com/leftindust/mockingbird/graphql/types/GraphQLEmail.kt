package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.dao.entity.Email

data class GraphQLEmail(
    val type: GraphQLEmailType,
    val email: String,
) {
    constructor(email: Email) : this(email.type, email.email)
}
