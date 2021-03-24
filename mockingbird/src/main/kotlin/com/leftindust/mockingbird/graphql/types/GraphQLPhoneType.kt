package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("PhoneType")
enum class GraphQLPhoneType {
    Work,
    Cell,
    Home,
    Pager,
    Other,
}