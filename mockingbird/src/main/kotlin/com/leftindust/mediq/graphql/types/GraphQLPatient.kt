package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.ContactDao
import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.dao.VisitDao
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Ethnicity
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.extensions.toInt
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
    val email: String? = null,
    val insuranceNumber: String? = null,
    val sex: Sex,
    val ethnicity: Ethnicity? = null,
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {
    private val authToken = authContext.mediqAuthToken

    constructor(patient: Patient, authContext: GraphQLAuthContext) : this(
        firstName = patient.firstName,
        middleName = patient.middleName,
        lastName = patient.lastName,
        phoneNumbers = listOf(patient.workPhone to GraphQLPhoneType.Work, patient.cellPhone to GraphQLPhoneType.Cell)
            .mapNotNull { (phone, type) -> phone?.let { GraphQLPhoneNumber(it, type) } },
        pid = gqlID(patient.pid),
        dateOfBirth = patient.dateOfBirth?.let { GraphQLTime(it) },
        address = patient.address,
        email = patient.email,
        insuranceNumber = patient.insuranceNumber,
        sex = patient.sex,
        ethnicity = patient.ethnicity,
        authContext = authContext,
    )

    suspend fun contacts(@GraphQLIgnore @Autowired contactDao: ContactDao): List<GraphQLPerson> =
        contactDao.getByPatient(pid.toInt(), authToken).getOrThrow().map { GraphQLEmergencyContact(it, authContext) }

    suspend fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> =
        doctorDao.getByPatient(pid.toInt(), authToken).getOrThrow().map { GraphQLDoctor(it, it.id!!, authContext) }

    suspend fun visits(@GraphQLIgnore @Autowired visitDao: VisitDao): List<GraphQLVisit> {
        return visitDao.getVisitsForPatientPid(pid.toInt(), authToken)
            .getOrThrow()
            .map { GraphQLVisit(it, it.id!!, authContext) } //safe nn assertion as we just got from DB}
    }
}