package com.leftindust.mediq.graphql.types.icd

import com.expediagroup.graphql.annotations.GraphQLName

@GraphQLName("IcdTerm")
data class GraphQLIcdTerm(
    val label: GraphQLIcdLanguageSpecificText,
    val foundationReference: String?,
    val linearizationReference: String?,
)
