package com.leftindust.mediq.external.icd

import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.external.HttpFailure
import com.leftindust.mediq.graphql.types.icd.*

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