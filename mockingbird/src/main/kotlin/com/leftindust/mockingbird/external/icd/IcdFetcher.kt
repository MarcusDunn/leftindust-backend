package com.leftindust.mockingbird.external.icd

import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.external.HttpFailure
import com.leftindust.mockingbird.graphql.types.icd.*

interface IcdFetcher {
    suspend fun search(
        query: String,
        flexiSearch: Boolean,
        flatResults: Boolean
    ): CustomResult<GraphQLIcdSearchResult, HttpFailure>

    suspend fun getDetails(code: FoundationIcdCode): CustomResult<GraphQLIcdFoundationEntity, HttpFailure>
    suspend fun getLinearizationEntity(releaseId: String, code: FoundationIcdCode): CustomResult<GraphQLIcdLinearizationEntity, HttpFailure>
    suspend fun linearization(linearizationName: String, code: FoundationIcdCode): CustomResult<GraphQLIcdMultiVersion, HttpFailure>
    suspend fun linearizationSearch(releaseId: String, linearizationName: String, query: String, flatResults: Boolean): CustomResult<GraphQLIcdSearchResult, HttpFailure>
}