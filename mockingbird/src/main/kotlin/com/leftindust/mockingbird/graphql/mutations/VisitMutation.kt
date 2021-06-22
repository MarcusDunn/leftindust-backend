package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import org.springframework.stereotype.Component

@Component
class VisitMutation(
    private val visitDao: VisitDao
) : Mutation {
    suspend fun addVisit(visit: GraphQLVisitInput, graphQLAuthContext: GraphQLAuthContext): GraphQLVisit {
        return visitDao
            .addVisit(visit, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLVisit(it, graphQLAuthContext) }
    }

    suspend fun editVisit(visit: GraphQLVisitEditInput, graphQLAuthContext: GraphQLAuthContext): GraphQLVisit {
        return visitDao
            .editVisit(visit, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLVisit(it, graphQLAuthContext) }
    }
}