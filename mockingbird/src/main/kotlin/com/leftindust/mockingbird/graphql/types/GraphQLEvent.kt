package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.extensions.toLong
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Event")
data class GraphQLEvent(
    val title: String,
    val description: String?,
    val startTime: GraphQLTime,
    val endTime: GraphQLTime,
    private val doctors: List<ID>,
    private val patients: List<ID>,
    private val authContext: GraphQLAuthContext
) {
    constructor(event: Event, doctors: List<ID>, patients: List<ID>, authContext: GraphQLAuthContext) : this(
        title = event.title,
        description = event.description,
        startTime = GraphQLTime(event.startTime),
        endTime = GraphQLTime(event.startTime.time + event.durationMillis),
        doctors,
        patients,
        authContext
    )

    suspend fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> {
        return doctors
            .map { doctorDao.getByDoctor(it.toLong(), authContext.mediqAuthToken) }
            .map { GraphQLDoctor(it, it.id!!, authContext) }
    }

    suspend fun patients(@GraphQLIgnore @Autowired patientDao: PatientDao): List<GraphQLPatient> {
        return patients
            .map { patientDao.getByPID(it.toLong(), authContext.mediqAuthToken) }
            .map { it.getOrThrow() }
            .map { GraphQLPatient(it, it.id!!, authContext) }
    }
}