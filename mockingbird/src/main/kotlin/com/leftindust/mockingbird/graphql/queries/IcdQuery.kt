package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.GraphQLFoundationIcdCode
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
        @GraphQLDescription("Cannot be empty string")
        query: String,
        flexiSearch: Boolean? = flexiSearchDefaultValue,
        flatResults: Boolean? = flatResultsDefaultValue,
        authContext: GraphQLAuthContext
    ): GraphQLIcdSearchResult {
        if (authContext.mediqAuthToken.isVerified()) {
            val nnFlexiSearch = flexiSearch ?: flexiSearchDefaultValue
            val nnFlatResults = flatResults ?: flatResultsDefaultValue
            return if (query.isNotEmpty()) {
                client
                    .search(query, nnFlexiSearch, nnFlatResults)
                    .let { searchResult ->
                        searchResult.copy(destinationEntities = searchResult.destinationEntities?.distinctBy {
                            it.id(
                                asUrl = true
                            )
                        })
                    }
            } else {
                throw GraphQLKotlinException("cannot query with empty string")
            }
        } else throw GraphQLKotlinException("not authorized")
    }

    suspend fun searchIcdFoundation(
        query: String,
        flexiSearch: Boolean? = flexiSearchDefaultValue,
        flatResults: Boolean? = flatResultsDefaultValue,
        authContext: GraphQLAuthContext
    ): List<GraphQLIcdFoundationEntity?>? {
        return searchIcd(query, flexiSearch, flatResults, authContext).destinationEntities?.map {
            it.entity(client)
        }
    }

    suspend fun icd(icdCode: String, authContext: GraphQLAuthContext): GraphQLIcdFoundationEntity {
        return if (authContext.mediqAuthToken.isVerified()) {
            client.getDetails(GraphQLFoundationIcdCode(icdCode))
        } else {
            throw NotAuthorizedException(authContext.mediqAuthToken, Crud.READ to Tables.IcdCode)
        }
    }
}