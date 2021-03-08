package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdGuessType")
enum class GraphQLIcdGuessType {
    Zero,
    One,
    Two;
}