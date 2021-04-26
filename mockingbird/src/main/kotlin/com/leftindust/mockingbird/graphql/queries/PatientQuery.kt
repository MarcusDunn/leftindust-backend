package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class PatientQuery(
    private val patientDao: PatientDao,
) : Query {

    suspend fun patients(
        range: GraphQLRangeInput? = null,
        pids: List<ID>? = null,
        sortedBy: Patient.SortableField? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLPatient> {
        return when {
            range != null && sortedBy != null -> {
                val intRange = range.toIntRange()
                patientDao
                    .getMany(intRange.first, intRange.last, sortedBy, authContext.mediqAuthToken)
            }
            range != null && sortedBy == null -> {
                val intRange = range.toIntRange()
                patientDao
                    .getMany(intRange.first, intRange.last, requester = authContext.mediqAuthToken)
            }
            pids != null && sortedBy != null ->
                patientDao.getPatientsByPids(pids, authContext.mediqAuthToken)
                    .sortedBy { sortedBy.instanceValue(it) }

            pids != null && sortedBy == null -> patientDao.getPatientsByPids(pids, authContext.mediqAuthToken)
            else -> throw GraphQLKotlinException("invalid arguments")
        }.map { GraphQLPatient(it, it.id!!, authContext) }
    }
}
