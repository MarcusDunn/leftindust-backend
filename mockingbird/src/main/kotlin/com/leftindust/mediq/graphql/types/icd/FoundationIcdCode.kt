package com.leftindust.mediq.graphql.types.icd

/**
 * takes in a url of a foundation entity or the actual foundation id (it parses both to the foundation id)
 */
data class FoundationIcdCode(private val url: String) {
    val value: String = url.split("/").last()
}