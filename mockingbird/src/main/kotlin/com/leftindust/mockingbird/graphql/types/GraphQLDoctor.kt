package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.extensions.toLong
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp

@GraphQLName("Doctor")
data class GraphQLDoctor(
    val did: ID,
    override val firstName: String,
    override val middleName: String? = null,
    override val lastName: String,
    override val phones: List<GraphQLPhone>,
    val title: String? = null,
    val dateOfBirth: GraphQLUtcTime? = null,
    val addresses: List<GraphQLAddress> = emptyList(),
    override val emails: List<GraphQLEmail> = emptyList(),
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    constructor(doctor: Doctor, id: Long, authContext: GraphQLAuthContext) : this(
        did = gqlID(id),
        firstName = doctor.nameInfo.firstName,
        middleName = doctor.nameInfo.middleName,
        lastName = doctor.nameInfo.lastName,
        phones = doctor.phone.map { GraphQLPhone(it) },
        title = doctor.title,
        dateOfBirth = GraphQLUtcTime(doctor.dateOfBirth),
        addresses = doctor.address.map { GraphQLAddress(it) },
        emails = doctor.email.map { GraphQLEmail(it) },
        authContext = authContext
    ) {
        if (doctor.id == null || doctor.id != id) {
            throw IllegalArgumentException("doctor.id does not match id where doctor.id is ${doctor.id} and id is $id")
        }
    }

    suspend fun user(@GraphQLIgnore @Autowired userDao: UserDao): GraphQLUser? {
        return userDao.findByDoctor(did, authContext.mediqAuthToken)?.let { GraphQLUser(it, authContext) }
    }

    suspend fun patients(@GraphQLIgnore @Autowired patientDao: PatientDao): List<GraphQLPatient> =
        patientDao.getByDoctor(did.toLong(), authToken).map { GraphQLPatient(it, it.id!!, authContext) }


    suspend fun schedule(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao,
        from: GraphQLUtcTime,
        to: GraphQLUtcTime
    ): List<GraphQLEvent> {
        return doctorDao.getByDoctor(did.toLong(), authToken)
            .schedule
            .getEventsBetween(Timestamp(from.unixMilliseconds), Timestamp(to.unixMilliseconds))
            .map { GraphQLEvent(event = it, authContext = authContext) }
    }
}