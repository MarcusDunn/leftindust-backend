package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.dao.PatientDao
import com.leftindust.mediq.dao.VisitDao
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.extensions.toLong
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp

@GraphQLName("Doctor")
data class GraphQLDoctor(
    val did: ID,
    override val firstName: String,
    override val middleName: String? = null,
    override val lastName: String,
    override val phoneNumbers: List<GraphQLPhoneNumber>,
    val title: String? = null,
    val dateOfBirth: GraphQLTime? = null,
    val address: String? = null,
    val email: String? = null,
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    constructor(doctor: Doctor, id: Long, authContext: GraphQLAuthContext) : this(
        did = gqlID(id),
        firstName = doctor.firstName,
        middleName = doctor.middleName,
        lastName = doctor.lastName,
        phoneNumbers = mapOf(doctor.cellPhone to GraphQLPhoneType.Cell, doctor.pagerNumber to GraphQLPhoneType.Pager)
            .map { GraphQLPhoneNumber(it.key ?: return@map null, it.value) }
            .filterNotNull(),
        title = doctor.title,
        dateOfBirth = doctor.dateOfBirth?.let { GraphQLTime(it) },
        address = doctor.address,
        email = doctor.email,
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