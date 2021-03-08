package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("Person")
interface GraphQLPerson {
    val firstName: String
    val middleName: String?
    val lastName: String
    val phoneNumbers: List<GraphQLPhoneNumber>
}