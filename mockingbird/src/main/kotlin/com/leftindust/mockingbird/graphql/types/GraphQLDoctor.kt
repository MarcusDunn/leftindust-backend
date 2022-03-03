package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.patient.PatientDao
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import java.util.*

@GraphQLName("Doctor")
data class GraphQLDoctor(
    val did: ID,
    override val firstName: String,
    override val middleName: String? = null,
    override val lastName: String,
    override val phones: List<GraphQLPhone>,
    override val thumbnail: String?,
    val title: String? = null,
    val dateOfBirth: GraphQLDate? = null,
    val addresses: List<GraphQLAddress> = emptyList(),
    override val emails: List<GraphQLEmail> = emptyList(),
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    @GraphQLName("DoctorId")
    data class ID(val id: UUID)

    constructor(doctor: Doctor, authContext: GraphQLAuthContext) : this(
        did = ID(doctor.id!!),
        firstName = doctor.nameInfo.firstName,
        middleName = doctor.nameInfo.middleName,
        lastName = doctor.nameInfo.lastName,
        phones = doctor.phones.map { GraphQLPhone(it) },
        thumbnail = doctor.thumbnail,
        title = doctor.title,
        dateOfBirth = doctor.dateOfBirth?.let { GraphQLDate(it.toLocalDate()) },
        addresses = doctor.addresses.map { GraphQLAddress(it) },
        emails = doctor.email.map { GraphQLEmail(it) },
        authContext = authContext
    )

    fun clinic(@GraphQLIgnore @Autowired clinicDao: ClinicDao): GraphQLClinic {
        TODO("not yet implemented $clinicDao")
    }

    suspend fun user(@GraphQLIgnore @Autowired userDao: UserDao): GraphQLUser? {
        return userDao
            .findByDoctor(did, authContext.mediqAuthToken)
            ?.let { GraphQLUser(it, authContext) }
    }

    suspend fun patients(@GraphQLIgnore @Autowired patientDao: PatientDao): List<GraphQLPatient> {
        return patientDao
            .getByDoctor(did, authToken)
            .map { GraphQLPatient(it, authContext) }
    }

    suspend fun events(@GraphQLIgnore @Autowired eventDao: EventDao): List<GraphQLEvent> {
        return eventDao
            .getByDoctor(did, authContext.mediqAuthToken)
            .map { event -> GraphQLEvent(event, authContext) }
    }

    suspend fun schedule(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao,
        from: GraphQLUtcTime,
        to: GraphQLUtcTime
    ): List<GraphQLEvent> {
        return doctorDao.getByDoctor(did, authToken)
            .getEventsBetween(Timestamp(from.unixMilliseconds), Timestamp(to.unixMilliseconds))
            .map { GraphQLEvent(event = it, authContext = authContext) }
    }
}