package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class VisitMutation(
    private val visitDao: VisitDao
) : Mutation {
    suspend fun addVisit(visit: GraphQLVisitInput, graphQLAuthContext: GraphQLAuthContext): GraphQLVisit {
        return visitDao.addVisit(visit, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLVisit(it, it.id!!, graphQLAuthContext) } // safe nn call as we just persisted this visit
    }
}