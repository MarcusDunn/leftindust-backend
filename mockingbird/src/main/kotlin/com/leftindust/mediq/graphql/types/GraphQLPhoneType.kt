package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("PhoneType")
enum class GraphQLPhoneType {
    Work,
    Cell,
    Home,
    Pager,
}