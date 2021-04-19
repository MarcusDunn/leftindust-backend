package com.leftindust.mockingbird.external.icd

import com.leftindust.mockingbird.graphql.types.icd.*

interface IcdFetcher {
    suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): GraphQLIcdSearchResult

    suspend fun getDetails(code: FoundationIcdCode): GraphQLIcdFoundationEntity
    suspend fun getLinearizationEntity(releaseId: String, code: FoundationIcdCode): GraphQLIcdLinearizationEntity
    suspend fun linearization(linearizationName: String, code: FoundationIcdCode): GraphQLIcdMultiVersion
    suspend fun linearizationSearch(
        releaseId: String,
        linearizationName: String,
        query: String,
        flatResults: Boolean
    ): GraphQLIcdSearchResult
}