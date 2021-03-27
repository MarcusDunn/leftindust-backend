package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.google.gson.JsonObject

@GraphQLName("JsonObject")
data class GraphQLJsonObject(
    val json: String
) {
    constructor(json: JsonObject) : this(json.toString())
}

