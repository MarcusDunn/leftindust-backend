package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode

data class GraphQLVisitInput(
    val event: ID,
    val title: String? = null,
    val description: String? = null,
    val foundationIcdCode: FoundationIcdCode,
    val doctor: ID,
    val patient: ID,
)
