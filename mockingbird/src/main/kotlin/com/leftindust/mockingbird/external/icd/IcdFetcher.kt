package com.leftindust.mockingbird.external.icd

import com.leftindust.mockingbird.graphql.types.icd.*
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput

interface IcdFetcher {
    suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): GraphQLIcdSearchResult

    suspend fun getDetails(code: FoundationIcdCode): GraphQLIcdFoundationEntity
    suspend fun getLinearizationEntity(releaseId: GraphQLReleaseIdInput, code: FoundationIcdCode): GraphQLIcdLinearizationEntity
    suspend fun linearization(linearizationName: String, code: FoundationIcdCode): GraphQLIcdMultiVersion
    suspend fun linearizationSearch(
        releaseId: GraphQLReleaseIdInput,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): GraphQLIcdSearchResult
}