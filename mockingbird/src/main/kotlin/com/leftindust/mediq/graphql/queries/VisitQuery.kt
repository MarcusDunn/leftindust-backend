package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.VisitDao
import com.leftindust.mediq.extensions.toInt
import com.leftindust.mediq.graphql.types.GraphQLVisit
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
        return visitDao.getVisitsForPatientPid(pid.toInt(), requester)
            .getOrThrow()
            .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
    }
}
