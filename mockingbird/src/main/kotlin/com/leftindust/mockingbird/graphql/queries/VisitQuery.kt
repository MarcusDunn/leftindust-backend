package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class VisitQuery(
    @Autowired private val visitDao: VisitDao
) : Query {
    suspend fun visits(
        vids: List<ID>? = null,
        pid: ID? = null,
        did: ID? = null,
        example: GraphQLVisitExample? = null,
        strict: Boolean = true,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLVisit> {
        return when {
            vids != null -> vids.map { vid ->
                visitDao
                    .getVisitByVid(vid.toLong(), graphQLAuthContext.mediqAuthToken)
                    .getOrThrow()
            }.map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            pid != null && did != null -> {
                val patientVisits = visits(pid = pid, graphQLAuthContext = graphQLAuthContext)
                val doctorVisits = visits(did = did, graphQLAuthContext = graphQLAuthContext)
                (patientVisits + doctorVisits).distinctBy { it.vid }
            }
            pid != null -> visitDao
                .getVisitsForPatientPid(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            did != null -> visitDao
                .getVisitsByDoctor(did.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            example != null -> visitDao
                .getByExample(example, strict, graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
            else -> throw GraphQLKotlinException("invalid arguments to visits")
        }
    }
}