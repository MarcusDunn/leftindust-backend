package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class VisitQuery(
    @Autowired private val visitDao: VisitDao,
    @Autowired private val eventDao: EventDao,
) : Query {
    suspend fun visits(
        vids: List<ID>? = null,
        pid: ID? = null,
        did: ID? = null,
        strict: Boolean = true,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLVisit> {
        return when {
            vids != null -> vids.map { vid ->
                visitDao
                    .getVisitByVid(vid.toLong(), graphQLAuthContext.mediqAuthToken)
            }.map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            pid != null && did != null -> {
                val patientVisits = visits(pid = pid, graphQLAuthContext = graphQLAuthContext)
                val doctorVisits = visits(did = did, graphQLAuthContext = graphQLAuthContext)
                (patientVisits + doctorVisits).distinctBy { it.vid }
            }
            pid != null -> eventDao
                .getByPatient(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                .map { visitDao.getByEvent(it.id!!, graphQLAuthContext.mediqAuthToken) }
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            did != null -> eventDao
                .getByDoctor(did.toLong(), graphQLAuthContext.mediqAuthToken)
                .map { visitDao.getByEvent(it.id!!, graphQLAuthContext.mediqAuthToken) }
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            else -> throw GraphQLKotlinException("invalid arguments to visits")
        }
    }
}