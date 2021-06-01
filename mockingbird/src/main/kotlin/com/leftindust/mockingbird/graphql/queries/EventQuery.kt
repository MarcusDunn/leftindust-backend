package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import org.springframework.stereotype.Component

@Component
class EventQuery(
    private val eventDao: EventDao,
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
) : Query {
    suspend fun events(
        doctors: List<GraphQLDoctor.ID>? = null,
        patients: List<GraphQLPatient.ID>? = null,
        range: GraphQLTimeRangeInput? = null,
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
            range != null -> {
                eventDao.getBetween(range, graphQLAuthContext.mediqAuthToken)
                    .map { GraphQLEvent(it, graphQLAuthContext) }
            }
            else -> throw IllegalArgumentException("invalid argument combination to events")
        }
    }

    private suspend fun getEvents(
        range: GraphQLTimeRangeInput,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLEvent> {
        return eventDao
            .getBetween(range = range, requester = graphQLAuthContext.mediqAuthToken)
            .map { GraphQLEvent(it, graphQLAuthContext) }
    }

    private suspend fun getEventsByPatient(
        patients: List<GraphQLPatient.ID>,
        graphQLAuthContext: GraphQLAuthContext,
    ): List<GraphQLEvent> {
        return patients
            .map { patientDao.getByPID(it, graphQLAuthContext.mediqAuthToken) }
            .flatMap { patient ->
                patient.events.map {
                    GraphQLEvent(
                        event = it,
                        authContext = graphQLAuthContext,
                    )
                } // this will break
            }
    }

    private suspend fun getEventsByDoctor(
        doctors: List<GraphQLDoctor.ID>,
        graphQLAuthContext: GraphQLAuthContext
    ) = doctors
        .map { doctorDao.getByDoctor(it, graphQLAuthContext.mediqAuthToken) }
        .flatMap { doc ->
            doc.events.map {
                GraphQLEvent(
                    event = it,
                    authContext = graphQLAuthContext,
                )
            } // also will break
        }
}