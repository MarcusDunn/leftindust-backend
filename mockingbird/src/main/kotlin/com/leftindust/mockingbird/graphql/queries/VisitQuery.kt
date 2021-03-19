package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class VisitQuery(
    @Autowired private val visitDao: VisitDao
) : Query {
    suspend fun visits(pid: ID? = null, did: ID? = null, graphQLAuthContext: GraphQLAuthContext): List<GraphQLVisit> {
        return when {
            pid != null -> visitDao
                .getVisitsForPatientPid(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
            did != null -> visitDao
                .getVisitsByDoctor(did.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
            else -> throw GraphQLKotlinException("invalid arguments to visits")
        }.map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
    }
}
