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
            }
            pid != null && did != null && strict -> {
                visitDao
                    .getVisitsForPatientPid(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                    .getOrThrow()
                    .filter { it.doctor.id!! == did.toLong() }
            }
            pid != null && did != null && !strict -> {
                val patientVisits = visitDao
                    .getVisitsForPatientPid(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                    .getOrThrow()
                val doctorVisits = visitDao
                    .getVisitsByDoctor(did.toLong(), graphQLAuthContext.mediqAuthToken)
                    .getOrThrow()
                (patientVisits + doctorVisits).toSet()
            }
            pid != null -> visitDao
                .getVisitsForPatientPid(pid.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
            did != null -> visitDao
                .getVisitsByDoctor(did.toLong(), graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
            example != null -> visitDao
                .getByExample(example, strict, graphQLAuthContext.mediqAuthToken)
                .getOrThrow()
            else -> throw GraphQLKotlinException("invalid arguments to visits")
        }.map { GraphQLVisit(it, it.id!!, graphQLAuthContext) }
    }
}
