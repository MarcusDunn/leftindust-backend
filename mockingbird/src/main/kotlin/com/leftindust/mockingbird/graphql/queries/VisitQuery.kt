package com.leftindust.mockingbird.graphql.queries

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
    suspend fun getVisitsByPatient(
        pid: ID,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLVisit> {
        val requester = graphQLAuthContext.mediqAuthToken
        return visitDao.getVisitsForPatientPid(pid.toLong(), requester)
            .getOrThrow()
            .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
    }
}
