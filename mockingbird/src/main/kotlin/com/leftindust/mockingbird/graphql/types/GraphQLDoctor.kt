package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.clinic.ReadClinicDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.patient.ReadPatientDao
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

    @GraphQLDescription(
        """
        The clinics this doctor is a member of.
    """
    )
    suspend fun clinic(
        @GraphQLIgnore @Autowired clinicDao: ReadClinicDao
    ): List<GraphQLClinic> = clinicDao
        .getByDoctor(did, authToken)
        .map { GraphQLClinic(it, authContext) }

    @GraphQLDescription(
        """
        The user associated with this doctor, if it exists.
    """
    )
    suspend fun user(
        @GraphQLIgnore @Autowired userDao: UserDao
    ): GraphQLUser? = userDao
        .findByDoctor(did, authContext.mediqAuthToken)
        ?.let { GraphQLUser(it, authContext) }

    @GraphQLDescription(
        """
        The patients this doctor takes care of.
    """
    )
    suspend fun patients(
        @GraphQLIgnore @Autowired patientDao: ReadPatientDao
    ): List<GraphQLPatient> = patientDao
        .getByDoctor(did, authToken)
        .map { GraphQLPatient(it, authContext) }


    @GraphQLDescription(
        """
        The events this doctor is a part of
    """
    )
    suspend fun events(
        @GraphQLIgnore @Autowired eventDao: EventDao
    ): List<GraphQLEvent> = eventDao
        .getByDoctor(did, authContext.mediqAuthToken)
        .map { event -> GraphQLEvent(event, authContext) }

    @GraphQLDescription(
        """
        The events this doctor is a part of between two times
    """
    )
    suspend fun schedule(
        @GraphQLIgnore @Autowired doctorDao: DoctorDao,
        from: GraphQLUtcTime,
        to: GraphQLUtcTime
    ): List<GraphQLEvent> = doctorDao.getByDoctor(did, authToken)
        .getEventsBetween(Timestamp(from.unixMilliseconds), Timestamp(to.unixMilliseconds))
        .map { GraphQLEvent(event = it, authContext = authContext) }
}