package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("AlwaysNull")
data class GraphQLAlwaysNull(val nothing: Int? = null)
