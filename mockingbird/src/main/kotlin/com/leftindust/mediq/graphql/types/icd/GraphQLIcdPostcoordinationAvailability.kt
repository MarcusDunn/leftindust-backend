package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdPostcoordinationAvailability")
enum class GraphQLIcdPostcoordinationAvailability {
    One,
    Two,
    Three
}