package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCodeInput

@GraphQLName("VisitInput")
data class GraphQLVisitInput(
    val event: ID,
    val title: String? = null,
    val description: String? = null,
    val foundationIcdCode: FoundationIcdCodeInput,
)