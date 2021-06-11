package com.leftindust.mockingbird.external.icd

import com.leftindust.mockingbird.graphql.types.icd.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput

interface IcdFetcher {
    suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): GraphQLIcdSearchResult

    suspend fun getDetails(code: GraphQLFoundationIcdCode): GraphQLIcdFoundationEntity
    suspend fun linearization(linearizationName: String, code: GraphQLFoundationIcdCode): GraphQLIcdMultiVersion
    suspend fun linearizationSearch(
        releaseId: GraphQLReleaseIdInput,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): GraphQLIcdSearchResult
}