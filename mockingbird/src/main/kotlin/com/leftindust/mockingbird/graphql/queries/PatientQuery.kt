package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPersonExample
import com.leftindust.mockingbird.graphql.types.examples.StringFilter
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
/**
 * Handles Graphql Patient Queries, all public methods in this class will appear in the schema (under query) due to
 * implementing Query.
 * @property patientDao handles integration with the database and authentication for DB requests
 */
class PatientQuery(
    private val patientDao: PatientDao,
) : Query {
    suspend fun patient(
        pid: ID,
        authContext: GraphQLAuthContext,
    ): GraphQLPatient {
        return patientDao
            .getByPID(pid.toLong(), authContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLPatient(it, it.id!!, authContext) }
    }

    suspend fun patients(
        range: GraphQLRangeInput? = null,
        pids: List<ID>? = null,
        sortedBy: Patient.SortableField? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLPatient> {
        return when {
            range != null && sortedBy != null -> {
                val intRange = range.toIntRange()
                patientDao.getMany(intRange.first, intRange.last, sortedBy, authContext.mediqAuthToken).getOrThrow()
            }
            range != null && sortedBy == null -> {
                val intRange = range.toIntRange()
                patientDao.getMany(intRange.first, intRange.last, requester = authContext.mediqAuthToken).getOrThrow()
            }
            pids != null && sortedBy != null -> {
                pids.map { patientDao.getByPID(it.toLong(), authContext.mediqAuthToken) }
                    .map { it.getOrThrow() }
                    .sortedBy { sortedBy.instanceValue(it) }
            }
            pids != null && sortedBy == null -> {
                pids.map { patientDao.getByPID(it.toLong(), authContext.mediqAuthToken) }.map { it.getOrThrow() }
            }
            else -> throw GraphQLKotlinException("invalid arguments")
        }.map { GraphQLPatient(it, it.id!!, authContext) }
    }

    @Throws(GraphQLKotlinException::class)
    suspend fun patientsGrouped(
        range: GraphQLRangeInput? = null,
        sortedBy: Patient.SortableField? = null,
        authContext: GraphQLAuthContext
    ): GraphQLPatientGroupedList {
        val requester = authContext.mediqAuthToken
        val validatedRange = (range ?: GraphQLRangeInput(0, 20)).toIntRange()
        val nnSortedBy = sortedBy ?: Patient.SortableField.PID
        return GraphQLPatientGroupedList(
            patientDao.getManyGroupedBySorted(validatedRange.first, validatedRange.last, nnSortedBy, requester)
                .getOrThrow()
                .mapValues { entry -> entry.value.map { GraphQLPatient(it, it.id!!, authContext) } }
        )
    }


    suspend fun searchPatientsByName(query: String, graphQLAuthContext: GraphQLAuthContext): List<GraphQLPatient> {
        return patientDao
            .searchByExample(
                GraphQLPatientExample(
                    GraphQLPersonExample(
                        firstName = StringFilter(includes = query),
                        middleName = StringFilter(includes = query),
                        lastName = StringFilter(includes = query),
                    )
                ),
                graphQLAuthContext.mediqAuthToken
            )
            .getOrThrow()
            .map { GraphQLPatient(it, it.id!!, graphQLAuthContext) }
    }

    suspend fun searchPatient(
        example: GraphQLPatientExample,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLPatient> {
        return patientDao.searchByExample(example, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .map { GraphQLPatient(it, it.id!!, graphQLAuthContext) }
    }


    @GraphQLName("PatientGroupedList")
    data class GraphQLPatientGroupedList(val groups: List<GraphQLLabeledGroup>) {

        constructor(map: Map<String, List<GraphQLPatient>>) : this(map.entries.map {
            GraphQLLabeledGroup(
                it.key,
                it.value,
            )
        })

        @GraphQLName("PatientLabeledGroup")
        data class GraphQLLabeledGroup(
            val groupName: String,
            val contents: List<GraphQLPatient>,
        )
    }

}
