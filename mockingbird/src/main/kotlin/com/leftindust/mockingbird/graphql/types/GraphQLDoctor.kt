package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.VisitDao
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
    val dateOfBirth: GraphQLTime? = null,
    val addresses: List<GraphQLAddress> = emptyList(),
    val emails: List<GraphQLEmail> = emptyList(),
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    constructor(doctor: Doctor, id: Long, authContext: GraphQLAuthContext) : this(
        did = gqlID(id),
        firstName = doctor.firstName,
        middleName = doctor.middleName,
        lastName = doctor.lastName,
        phones = doctor.phones.map { GraphQLPhone(it) },
        title = doctor.title,
        dateOfBirth = GraphQLTime(doctor.dateOfBirth),
        addresses = doctor.addresses.map { GraphQLAddress(it) },
        emails = doctor.emails.map { GraphQLEmail(it) },
        authContext = authContext
    ) {
        assert(doctor.id == null || doctor.id == id)
    }

    suspend fun patients(@GraphQLIgnore @Autowired patientDao: PatientDao): List<GraphQLPatient> =
        patientDao.getByDoctor(did.toLong(), authToken).getOrThrow().map { GraphQLPatient(it, it.id!!, authContext) }


    suspend fun visits(@GraphQLIgnore @Autowired visitDao: VisitDao): List<GraphQLVisit> {
        return visitDao.getVisitsByDoctor(did.toLong(), authToken)
            .getOrThrow()
            .map { GraphQLVisit(it, it.id!!, authContext) } // safe nn assertion as we just got from DB
    }

    suspend fun schedule(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao,
        from: GraphQLTime,
        to: GraphQLTime
    ): List<GraphQLEvent> {
        return doctorDao.getByDoctor(did.toLong(), authToken).getOrThrow()
            .getEventsBetween(Timestamp(from.unixMilliseconds), Timestamp(to.unixMilliseconds))
            .map { GraphQLEvent(it, authContext) }
    }
}