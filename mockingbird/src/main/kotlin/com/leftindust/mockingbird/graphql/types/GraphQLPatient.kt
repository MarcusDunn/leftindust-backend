package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.address.ReadAddressDao
import com.leftindust.mockingbird.dao.email.ReadEmailDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.phone.ReadPhoneDao
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLName("Patient")
data class GraphQLPatient(
    override val firstName: String,
    override val middleName: String? = null,
    override val lastName: String,
    @GraphQLDescription(GraphQLPerson.thumbnailDescription)
    override val thumbnail: String? = null,
    val pid: ID,
    val dateOfBirth: GraphQLDate,
    val insuranceNumber: String? = null,
    val sex: Sex,
    val gender: String = sex.toString(),
    val ethnicity: Ethnicity? = null,
    private val authContext: GraphQLAuthContext
) : GraphQLPerson {

    @GraphQLName("PatientId")
    data class ID(val id: UUID)

    constructor(patient: Patient, authContext: GraphQLAuthContext) : this(
        pid = ID(patient.id!!),
        firstName = patient.nameInfo.firstName,
        middleName = patient.nameInfo.middleName,
        lastName = patient.nameInfo.lastName,
        dateOfBirth = GraphQLDate(patient.dateOfBirth.toLocalDate()),
        insuranceNumber = patient.insuranceNumber,
        sex = patient.sex,
        gender = patient.gender,
        ethnicity = patient.ethnicity,
        authContext = authContext,
        thumbnail = patient.thumbnail,
    )

    suspend fun contacts(@GraphQLIgnore @Autowired contactDao: ContactDao): List<GraphQLPerson> = contactDao
        .getPatientContacts(pid, authContext.mediqAuthToken)
        .map { GraphQLEmergencyContact(it, authContext) }

    suspend fun doctors(@GraphQLIgnore @Autowired doctorDao: DoctorDao): List<GraphQLDoctor> = doctorDao
        .getPatientDoctors(pid, authContext.mediqAuthToken)
        .map { GraphQLDoctor(it, authContext) }

    suspend fun visits(@GraphQLIgnore @Autowired visitDao: VisitDao): List<GraphQLVisit> = visitDao
        .getPatientVisits(pid, authContext.mediqAuthToken)
        .map { visit -> GraphQLVisit(visit, authContext) }

    suspend fun user(@GraphQLIgnore @Autowired userDao: UserDao): GraphQLUser? = userDao
        .findPatientUser(pid, authContext.mediqAuthToken)
        ?.let { GraphQLUser(it, authContext) }

    suspend fun events(@GraphQLIgnore @Autowired eventDao: EventDao): List<GraphQLEvent> = eventDao
        .getPatientEvents(pid, authContext.mediqAuthToken)
        .map { event -> GraphQLEvent(event, authContext) }

    suspend fun assignedForms(@GraphQLIgnore @Autowired formDao: FormDao): List<GraphQLAssignedForm> = formDao
        .getByPatientAssigned(pid, authContext.mediqAuthToken)
        .map { GraphQLAssignedForm(it, authContext) }

    override suspend fun phones(@GraphQLIgnore @Autowired phoneDao: ReadPhoneDao): List<GraphQLPhone> = phoneDao
        .getPatientPhones(pid, authContext)
        .map { GraphQLPhone(it) }

    override suspend fun emails(@GraphQLIgnore @Autowired emailDao: ReadEmailDao): List<GraphQLEmail> = emailDao
        .getPatientEmails(pid, authContext)
        .map { GraphQLEmail(it) }

    suspend fun addresses(@GraphQLIgnore @Autowired addressDao: ReadAddressDao): List<GraphQLAddress> = addressDao
        .getPatientAddresses(pid, authContext)
        .map { GraphQLAddress(it) }
}