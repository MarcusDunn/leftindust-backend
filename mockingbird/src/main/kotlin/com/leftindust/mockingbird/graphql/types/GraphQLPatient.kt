package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.extensions.toLong
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("Patient")
data class GraphQLPatient(
    override val firstName: String,
    override val middleName: String? = null,
    override val lastName: String,
    override val phoneNumbers: List<GraphQLPhoneNumber> = emptyList(),
    val pid: ID,
    val dateOfBirth: GraphQLTime? = null,
    val address: String? = null,
    val emails: List<GraphQLEmail> = emptyList(),
    val insuranceNumber: String? = null,
    val sex: Sex,
    val ethnicity: Ethnicity? = null,
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    constructor(patient: Patient, id: Long, authContext: GraphQLAuthContext) : this(
        pid = gqlID(id),
        firstName = patient.firstName,
        middleName = patient.middleName,
        lastName = patient.lastName,
        phoneNumbers = patient.phones.map { GraphQLPhoneNumber(it) },
        dateOfBirth = GraphQLTime(patient.dateOfBirth),
        address = patient.address,
        emails = patient.emails.map { GraphQLEmail(it) },
        insuranceNumber = patient.insuranceNumber,
        sex = patient.sex,
        ethnicity = patient.ethnicity,
        authContext = authContext,
    ) {
        assert(patient.id == null || patient.id == id)
    }

    suspend fun contacts(@GraphQLIgnore @Autowired contactDao: ContactDao): List<GraphQLPerson> =
        contactDao.getByPatient(pid.toLong(), authToken).getOrThrow().map { GraphQLEmergencyContact(it, authContext) }

    suspend fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> =
        doctorDao.getByPatient(pid.toLong(), authToken).getOrThrow().map { GraphQLDoctor(it, it.id!!, authContext) }

    suspend fun visits(@GraphQLIgnore @Autowired visitDao: VisitDao): List<GraphQLVisit> {
        return visitDao.getVisitsForPatientPid(pid.toLong(), authToken)
            .getOrThrow()
            .map { GraphQLVisit(it, it.id!!, authContext) } // safe nn assertion as we just got from DB
    }
}