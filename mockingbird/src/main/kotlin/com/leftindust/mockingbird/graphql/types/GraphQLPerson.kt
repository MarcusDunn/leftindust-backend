package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("Person")
interface GraphQLPerson {
    val firstName: String
    val middleName: String?
    val lastName: String
    val phones: List<GraphQLPhone>
}