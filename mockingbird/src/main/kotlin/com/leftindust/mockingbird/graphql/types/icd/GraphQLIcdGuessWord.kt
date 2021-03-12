package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdGuessWord")
data class GraphQLIcdGuessWord(
    val label: String?,
    val dontChangeResult: String,
)
