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
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdLinearizationEntity
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSearchResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
import org.springframework.stereotype.Component

@Component
class IcdQuery(
    private val client: IcdFetcher,
) : Query {
    private val flexiSearchDefaultValue = true
    private val flatResultsDefaultValue = false
    private val graphQLReleaseIdInputDefaultValue = GraphQLReleaseIdInput.R_2020_09


    suspend fun searchIcd(
        @GraphQLDescription("Cannot be empty string")
        query: String,
        flexiSearch: Boolean? = flexiSearchDefaultValue,
        flatResults: Boolean? = flatResultsDefaultValue,
        authContext: GraphQLAuthContext
    ): GraphQLIcdSearchResult {
        if (authContext.mediqAuthToken.isVerified()) {
            val nnFlatResults = flatResults ?: flatResultsDefaultValue
            return if (query.isNotEmpty()) {
                client
                    .linearizationSearch(query, "mms", nnFlatResults)
                    .let { searchResult ->
                        searchResult.copy(destinationEntities = searchResult.destinationEntities?.distinctBy {
                            it.urlId(
                                asUrl = true
                            )
                        })
                    }
            } else {
                throw GraphQLKotlinException("cannot query with empty string")
            }
        } else throw GraphQLKotlinException("not authorized")
    }

    suspend fun icd(
        icdCode: String,
        authContext: GraphQLAuthContext
    ): GraphQLIcdLinearizationEntity {
        return if (authContext.mediqAuthToken.isVerified()) {
            client.linearizationEntity(GraphQLFoundationIcdCode(icdCode))
        } else {
            throw NotAuthorizedException(authContext.mediqAuthToken, Crud.READ to Tables.IcdCode)
        }
    }
}