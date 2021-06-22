package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLPatientExample
import org.springframework.stereotype.Component

@Component
class PatientQuery(
    private val patientDao: PatientDao,
) : Query {
    suspend fun patients(
        range: GraphQLRangeInput? = null,
        pids: List<GraphQLPatient.ID>? = null,
        sortedBy: Patient.SortableField? = null,
        example: GraphQLPatientExample? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLPatient> {
        return when {
            range != null && sortedBy != null && example == null && pids == null -> patientDao
                .getMany(range, sortedBy, authContext.mediqAuthToken)
            range != null && sortedBy == null && pids == null && example == null -> patientDao
                .getMany(range, requester = authContext.mediqAuthToken)
            pids != null && sortedBy != null && example == null && range == null -> patientDao
                .getPatientsByPids(pids, authContext.mediqAuthToken)
                .sortedBy { sortedBy.instanceValue(it) }
            pids != null && sortedBy == null && example == null && range == null -> patientDao
                .getPatientsByPids(pids, authContext.mediqAuthToken)
            example != null && pids == null && sortedBy == null && range == null -> patientDao
                .searchByExample(example, authContext.mediqAuthToken)
                .distinctBy { it.id }
            else -> throw GraphQLKotlinException("invalid arguments")
        }.map { GraphQLPatient(it, authContext) }
    }
}
