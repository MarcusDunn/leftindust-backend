package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.external.icd.IcdFetcher
import com.leftindust.mediq.graphql.types.icd.GraphQLIcdFoundationEntity
import com.leftindust.mediq.graphql.types.icd.GraphQLIcdSearchResult
import org.springframework.stereotype.Component

@Component
class IcdQuery(
    private val client: IcdFetcher,
) : Query {
    private val flexiSearchDefaultValue = true
    private val flatResultsDefaultValue = false

    /**
     * searches the ICD database for a given query
     * @param query the string to look up against Icd code details
     * @returns the [GraphQLIcdSearchResult] for the given query
     */
    suspend fun searchIcd(
        query: String,
        flexiSearch: Boolean? = flexiSearchDefaultValue,
        flatResults: Boolean? = flatResultsDefaultValue,
        authContext: GraphQLAuthContext
    ): GraphQLIcdSearchResult {
        if (authContext.mediqAuthToken.isVerified()) {
            val nnFlexiSearch = flexiSearch ?: flexiSearchDefaultValue
            val nnFlatResults = flatResults ?: flatResultsDefaultValue
            return client
                .search(query, nnFlexiSearch, nnFlatResults)
                .getOrThrow()
        } else throw GraphQLKotlinException("not authorized")
    }

    suspend fun searchIcdLinearization(
        query: String,
        releaseId: String? = null,
        linearizationName: String? = null,
        flatResults: Boolean? = null,
        authContext: GraphQLAuthContext
    ): GraphQLIcdSearchResult {
        if (authContext.mediqAuthToken.isVerified()) {
            return client
                .linearizationSearch(releaseId ?: "2020-09", linearizationName ?: "mms", query, flatResults ?: false)
                .getOrThrow()
        } else throw GraphQLKotlinException("not authorized")
    }

    /**
     * finds details on the given [FoundationIcdCode]
     * @param icdCode the desired ICD code
     * @returns the [GraphQLIcdFoundationEntity] representation of the [icdCode]
     */
    suspend fun icd(icdCode: String, authContext: GraphQLAuthContext): GraphQLIcdFoundationEntity {
        if (authContext.mediqAuthToken.isVerified()) {
            return client
                .getDetails(FoundationIcdCode(icdCode))
                .getOrThrow()
        } else throw GraphQLKotlinException("not authorized")
    }
}