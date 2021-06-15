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
        events: List<GraphQLEvent.ID>? = null,
        doctors: List<GraphQLDoctor.ID>? = null,
        patients: List<GraphQLPatient.ID>? = null,
        range: GraphQLTimeRangeInput? = null,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLEvent> {
        return when {
            doctors != null && patients != null && range == null && events == null -> {
                val patientEvents = getEventsByPatient(patients = patients, graphQLAuthContext = graphQLAuthContext)
                val doctorEvents = getEventsByDoctor(doctors = doctors, graphQLAuthContext = graphQLAuthContext)
                listOf(patientEvents, doctorEvents).flatten()
            }
            doctors != null && patients == null && range == null && events == null -> getEventsByDoctor(
                doctors,
                graphQLAuthContext
            )
            patients != null && doctors == null && range == null && events == null -> getEventsByPatient(
                patients,
                graphQLAuthContext
            )
            range != null && patients == null && doctors == null && events == null -> eventDao
                .getBetween(range, graphQLAuthContext.mediqAuthToken)
                .map { GraphQLEvent(it, graphQLAuthContext) }
            events != null && doctors == null && patients == null && range == null -> events
                .map { eventDao.getById(it, graphQLAuthContext.mediqAuthToken) }
                .map { GraphQLEvent(it, graphQLAuthContext) }
            else -> throw IllegalArgumentException("invalid argument combination to events")
        }
    }

    private suspend fun getEventsByPatient(
        patients: List<GraphQLPatient.ID>,
        graphQLAuthContext: GraphQLAuthContext,
    ): List<GraphQLEvent> {
        return patients
            .map { eventDao.getByPatient(it, graphQLAuthContext.mediqAuthToken) }
            .flatMap { event ->
                event.map {
                    GraphQLEvent(
                        event = it,
                        authContext = graphQLAuthContext,
                    )
                }
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