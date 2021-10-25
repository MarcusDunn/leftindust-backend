package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName


@GraphQLName("FoundationIcdCode")
data class GraphQLFoundationIcdCode(val url: String) {
    val code: String =
        Regex("""\d{5,}""").find(url)?.value ?: throw IllegalArgumentException("the url must contain a code")
}

@GraphQLName("FoundationIcdCodeInput")
data class GraphQLFoundationIcdCodeInput(
    @GraphQLDescription("""full A ICD-11 Foundation Code URL""")
    val url: String
)