package com.leftindust.mockingbird.graphql.types.icd

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

/**
 * takes in a url of a foundation entity or the actual foundation id (it parses both to the foundation id)
 */
data class FoundationIcdCode(private val url: String) {
    constructor(foundationIcdCode: FoundationIcdCodeInput) : this(foundationIcdCode.url)

    val value: String = url.split("/").last()
}

data class FoundationIcdCodeInput(val code: String) {
    @GraphQLIgnore
    val url: String = code //todo
}