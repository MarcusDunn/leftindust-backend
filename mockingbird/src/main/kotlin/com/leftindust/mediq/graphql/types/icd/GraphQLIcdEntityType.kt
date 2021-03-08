package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdEntityType")
enum class GraphQLIcdEntityType {
    Zero,
    One,
    Two;
}