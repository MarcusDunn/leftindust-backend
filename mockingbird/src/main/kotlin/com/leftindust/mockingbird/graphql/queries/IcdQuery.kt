package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdFoundationEntity
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSearchResult
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
        } else {
            throw NotAuthorizedException(authContext.mediqAuthToken, Crud.READ to Tables.IcdCode)
        }
    }

    suspend fun icd(icdCode: String, authContext: GraphQLAuthContext): GraphQLIcdFoundationEntity {
        return if (authContext.mediqAuthToken.isVerified()) {
            client.getDetails(FoundationIcdCode(icdCode))
        } else {
            throw NotAuthorizedException(authContext.mediqAuthToken, Crud.READ to Tables.IcdCode)
        }
    }
}