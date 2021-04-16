package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import org.springframework.stereotype.Component

@Component
class EventQuery(
    private val eventDao: EventDao,
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
) : Query {
    suspend fun events(
        doctors: List<ID>? = null,
        patients: List<ID>? = null,
        range: GraphQLTimeRangeInput? = null, // todo make not-nullable
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLEvent> {
        return when {
            doctors != null && patients != null -> {
                val patientEvents = getEventsByPatient(patients = patients, graphQLAuthContext = graphQLAuthContext)
                val doctorEvents = getEventsByDoctor(doctors = doctors, graphQLAuthContext = graphQLAuthContext)
                listOf(patientEvents, doctorEvents).flatten()
            }
            doctors != null -> getEventsByDoctor(doctors, graphQLAuthContext)
            patients != null -> getEventsByPatient(patients, graphQLAuthContext)
            else -> throw IllegalArgumentException("invalid argument combination to events")
        }
    }

    private suspend fun getEvents(
        range: GraphQLTimeRangeInput,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLEvent> {
        return eventDao
            .getMany(range = range, requester = graphQLAuthContext.mediqAuthToken)
            .map { GraphQLEvent(it, graphQLAuthContext) }
    }

    private suspend fun getEventsByPatient(
        patients: List<ID>,
        graphQLAuthContext: GraphQLAuthContext,
    ): List<GraphQLEvent> {
        return patients
            .map { patientDao.getByPID(it.toLong(), graphQLAuthContext.mediqAuthToken) }
            .map { it.getOrThrow() }
            .flatMap { patient ->
                patient.schedule.events.map {
                    GraphQLEvent(
                        event = it,
                        authContext = graphQLAuthContext,
                    )
                } // this will break
            }
    }

    private suspend fun getEventsByDoctor(
        doctors: List<ID>,
        graphQLAuthContext: GraphQLAuthContext
    ) = doctors
        .map { doctorDao.getByDoctor(it.toLong(), graphQLAuthContext.mediqAuthToken) }
        .flatMap { doc ->
            doc.schedule.events.map {
                GraphQLEvent(
                    event = it,
                    authContext = graphQLAuthContext,
                )
            } // also will break
        }
}