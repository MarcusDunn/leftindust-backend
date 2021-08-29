package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName

@GraphQLName("Person")
interface GraphQLPerson {
    val firstName: String
    val middleName: String?
    val lastName: String
    val phones: List<GraphQLPhone>
    val emails: List<GraphQLEmail>

    @GraphQLDescription(thumbnailDescription)
    val thumbnail: String?

    companion object {
        const val thumbnailDescription = "a base64 icon. Cannot be over 10 000 characters when base64 encoded"
    }
}