package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdSimplePropertyValue")
data class GraphQLIcdSimplePropertyValue(
    val propertyId: String?,
    val label: String?,
    val score: Double?,
    val important: Boolean?
)