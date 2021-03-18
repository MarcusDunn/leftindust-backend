package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class PatientQuery(
    private val patientDao: PatientDao,
) : Query {

    suspend fun patients(
        range: GraphQLRangeInput? = null,
        pids: List<ID>? = null,
        example: GraphQLPatientExample? = null,
        sortedBy: Patient.SortableField? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLPatient> {
        return when {
            range != null && sortedBy != null -> {
                val intRange = range.toIntRange()
                patientDao
                    .getMany(intRange.first, intRange.last, sortedBy, authContext.mediqAuthToken)
                    .getOrThrow()
            }
            range != null && sortedBy == null -> {
                val intRange = range.toIntRange()
                patientDao
                    .getMany(intRange.first, intRange.last, requester = authContext.mediqAuthToken)
                    .getOrThrow()
            }
            pids != null && sortedBy != null -> {
                pids.map { patientDao.getByPID(it.toLong(), authContext.mediqAuthToken) }
                    .map { it.getOrThrow() }
                    .sortedBy { sortedBy.instanceValue(it) }
            }
            pids != null && sortedBy == null -> {
                pids.map { patientDao.getByPID(it.toLong(), authContext.mediqAuthToken) }
                    .map { it.getOrThrow() }
            }
            example != null && sortedBy != null -> {
                patientDao
                    .searchByExample(example, authContext.mediqAuthToken)
                    .getOrThrow()
                    .sortedBy { sortedBy.instanceValue(it) }
            }
            example != null && sortedBy == null -> {
                patientDao
                    .searchByExample(example, authContext.mediqAuthToken)
                    .getOrThrow()
            }
            else -> throw GraphQLKotlinException("invalid arguments")
        }.map { GraphQLPatient(it, it.id!!, authContext) }
    }
}
