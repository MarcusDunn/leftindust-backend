package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.impl.repository.*
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLMonth
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLNameInfoInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PatientDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val doctorPatientRepository = mockk<HibernateDoctorPatientRepository>()
    private val visitRepository = mockk<HibernateVisitRepository>()
    private val sessionFactory = mockk<SessionFactory>()
    private val eventRepository = mockk<HibernateEventRepository>()

    @Test
    fun getByPID() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(any()) } returns mockkPatient

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,
        )
        val actual = runBlocking { patientDaoImpl.getByPID(1000, mockk()) }

        assertEquals(mockkPatient, actual)

    }

    @Test
    fun addNewPatient() {
        val graphQLPatientInput = GraphQLPatientInput(
            nameInfo = GraphQLNameInfoInput(
                firstName = "hello",
                lastName = "world",
            ),
            dateOfBirth = GraphQLDateInput(year = 2020, day = 10, month = GraphQLMonth.Feb),
            sex = Sex.Male
        )
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.save(any()) } returns mockkPatient
        every { sessionFactory.currentSession } returns mockk()

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,

            )

        val actual = runBlocking { patientDaoImpl.addNewPatient(graphQLPatientInput, mockk()) }

        assertEquals(mockkPatient, actual)
    }


    @Test
    fun removePatientByPID() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(1000L) } returns mockkPatient
        every { patientRepository.delete(any()) } returns Unit


        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,
        )

        val actual = runBlocking { patientDaoImpl.removeByPID(1000L, mockk()) }

        assertEquals(mockkPatient, actual)
    }

    @Test
    fun getByDoctor() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getOne(1000L) } returns mockk {
            every { id } returns 100L
        }
        every { doctorPatientRepository.getAllByDoctorId(100L) } returns setOf(mockk {
            every { patient } returns mockkPatient
        })

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,
        )

        val actual = runBlocking { patientDaoImpl.getByDoctor(1000L, mockk()) }

        assertEquals(listOf(mockkPatient), actual)
    }

    @Test
    fun getByVisit() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { visitRepository.getOne(1000L) } returns mockk {
            every { event } returns mockk {
                every { patients } returns setOf(mockkPatient)
            }
        }

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,
        )

        val actual = runBlocking { patientDaoImpl.getByVisit(1000L, mockk()) }

        assertEquals(listOf(mockkPatient), actual.toList())
    }

    @Test
    fun addDoctorToPatient() {
        val mockkPatient = mockk<Patient>()
        val mockkDoctor = mockk<Doctor>()

        every { mockkPatient.addDoctor(mockkDoctor) } returns mockkPatient

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(1000) } returns mockkPatient
        every { doctorRepository.getOne(1001) } returns mockkDoctor

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,

            )

        val actual = runBlocking { patientDaoImpl.addDoctorToPatient(ID("1000"), ID("1001"), mockk()) }

        assertEquals(mockkPatient, actual)
    }

    @Test
    fun update() {
        val mockkPatient = mockk<Patient> {
            every { setByGqlInput(any(), any()) } returns Unit
        }
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(100) } returns mockkPatient

        every { sessionFactory.currentSession } returns mockk()

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, eventRepository, visitRepository, sessionFactory,
        )

        val patientInput = mockk<GraphQLPatientEditInput> {
            every { pid } returns gqlID(100)
        }

        val actual = runBlocking { patientDaoImpl.update(patientInput, mockk()) }

        assertEquals(mockkPatient, actual)
    }

    @Test
    fun removeByPID() {
        // TODO: 2021-04-26  
    }

    @Test
    fun getMany() {
        // TODO: 2021-04-26  
    }

    @Test
    fun getPatientsByPids() {
        // TODO: 2021-04-26  
    }
}