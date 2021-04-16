package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Event
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Event")
data class GraphQLEvent(
    val eid: Long,
    val title: String,
    val description: String?,
    val startTime: GraphQLUtcTime?,
    val endTime: GraphQLUtcTime?,
    val allDay: Boolean,
    val reoccurrence: GraphQLRecurrence?,
    private val authContext: GraphQLAuthContext,
) {
    constructor(event: Event, authContext: GraphQLAuthContext) : this(
        eid = event.id!!.toLong(),
        title = event.title,
        description = event.description,
        startTime = GraphQLUtcTime(event.startTime),
        endTime = event.endTime?.let { GraphQLUtcTime(it) },
        allDay = event.allDay,
        reoccurrence = event.reoccurrence?.let { GraphQLRecurrence(it) },
        authContext = authContext
    )

    suspend fun doctors(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao
    ): List<GraphQLDoctor> {
        return doctorDao.getByEvent(eid, authContext.mediqAuthToken)
            .map { GraphQLDoctor(it, it.id!!, authContext) }
    }

    suspend fun patients(@GraphQLIgnore @Autowired patientDao: PatientDao): List<GraphQLPatient> {
        return patientDao.getByEvent(eid, authContext.mediqAuthToken)
            .map { GraphQLPatient(it, it.id!!, authContext) }
    }
}