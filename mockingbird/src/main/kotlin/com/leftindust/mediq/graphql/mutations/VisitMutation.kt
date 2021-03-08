package com.leftindust.mediq.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.VisitDao
import com.leftindust.mediq.graphql.types.GraphQLVisit
import com.leftindust.mediq.graphql.types.GraphQLVisitInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class VisitMutation(
    @Autowired private val visitDao: VisitDao
) : Mutation {
    suspend fun addVisit(visit: GraphQLVisitInput, graphQLAuthContext: GraphQLAuthContext): GraphQLVisit {
        return visitDao.addVisit(visit, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLVisit(it, it.id!!, graphQLAuthContext) } // safe nn call as we just persisted this visit
    }
}