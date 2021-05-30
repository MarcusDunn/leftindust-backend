package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.DoctorPatient
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.*
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DoctorDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val doctorPatientRepository = mockk<HibernateDoctorPatientRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val eventRepository = mockk<HibernateEventRepository>()
    private val clinicRepository = mockk<HibernateClinicRepository>()
    private val sessionFactory = mockk<SessionFactory>()


    @Test
    fun getByPatient() {
        val mockkDoctor = mockk<Doctor>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val patientID = UUID.randomUUID()

        val mockkPatient = mockk<Patient> {
            every { id } returns patientID
        }
        every { patientRepository.getById(any()) } returns mockkPatient
        val doctorPatient = mockk<DoctorPatient> {
            every { doctor } returns mockkDoctor
        }
        coEvery { doctorPatientRepository.getAllByPatientId(patientID) } returns setOf(doctorPatient)


        val doctorDaoImpl = DoctorDaoImpl(
            authorizer, doctorRepository, doctorPatientRepository,
            patientRepository, eventRepository, clinicRepository, sessionFactory
        )

        val actual = runBlocking { doctorDaoImpl.getByPatient(GraphQLPatient.ID(patientID), mockk()) }

        assertEquals(listOf(mockkDoctor), actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            patientRepository.getById(patientID)
            mockkPatient.id
            doctorPatientRepository.getAllByPatientId(patientID)
            doctorPatient.doctor
        }
    }

    @Test
    fun getByEvent() {
        val mockkDoctor = mockk<Doctor>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        val mockkEvent = mockk<Event> {
            every { doctors } returns mutableSetOf(mockkDoctor)
        }
        val eventID = UUID.randomUUID()

        every { eventRepository.getById(eventID) } returns mockkEvent

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                clinicRepository, sessionFactory
            )
        val actual = runBlocking { doctorDaoImpl.getByEvent(GraphQLEvent.ID(eventID), mockk()) }

        assertEquals(setOf(mockkDoctor), actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            mockkEvent.doctors
            eventRepository.getById(eventID)
        }
    }

    @Test
    fun getByDoctor() {
        val mockkDoctor = mockk<Doctor>()
        val doctorID = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getById(doctorID) } returns mockkDoctor

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                clinicRepository, sessionFactory
            )
        val actual = runBlocking { doctorDaoImpl.getByDoctor(GraphQLDoctor.ID(doctorID), mockk()) }

        assertEquals(mockkDoctor, actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            doctorRepository.getById(doctorID)
        }
    }

    @Test
    fun addDoctor() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val doctor = mockk<Doctor>()
        every { doctorRepository.save(any()) } returns doctor

        val graphQLDoctorInput = mockk<GraphQLDoctorInput>(relaxed = true)

        every { patientRepository.findAllById(emptyList()) } returns emptyList<Patient>()

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                clinicRepository,
                sessionFactory
            )

        val result = runBlocking { doctorDaoImpl.addDoctor(graphQLDoctorInput, mockk()) }

        assertEquals(doctor, result)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            doctorRepository.save(any())
        }
    }

    @Test
    fun editDoctor() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val doctorID = UUID.randomUUID()

        val graphQLDoctorInput = mockk<GraphQLDoctorEditInput>() {
            every { did } returns GraphQLDoctor.ID(doctorID)
        }

        val mockkDoctor = mockk<Doctor>() {
            every { setByGqlInput(graphQLDoctorInput, any()) } just runs
        }


        every { doctorRepository.getById(doctorID) } returns mockkDoctor

        every { sessionFactory.currentSession } returns mockk()

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                clinicRepository, sessionFactory
            )

        val result = runBlocking { doctorDaoImpl.editDoctor(graphQLDoctorInput, mockk()) }

        verifyAll {
            mockkDoctor.setByGqlInput(graphQLDoctorInput, any())
        }

        assertEquals(mockkDoctor, result)
    }

    @Test
    internal fun `edit doctor with insufficient perms`() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Denied

        val doctorID = UUID.randomUUID()


        val graphQLDoctorInput = mockk<GraphQLDoctorEditInput>() {
            every { did } returns GraphQLDoctor.ID(doctorID)
        }

        val mockkDoctor = mockk<Doctor>() {
            every { setByGqlInput(graphQLDoctorInput, any()) } just runs
        }

        every { doctorRepository.getById(doctorID) } returns mockkDoctor

        every { sessionFactory.currentSession } returns mockk()

        val doctorDaoImpl = DoctorDaoImpl(
            authorizer,
            doctorRepository,
            doctorPatientRepository,
            patientRepository,
            eventRepository,
            clinicRepository, sessionFactory
        )

        assertThrows<NotAuthorizedException> {
            runBlocking { doctorDaoImpl.editDoctor(graphQLDoctorInput, mockk()) }
        }
    }

    @Test
    fun getByClinic() {
        val clinicId = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val mockkDoctor = mockk<Doctor>()

        every { clinicRepository.getById(clinicId) } returns mockk() {
            every { doctors } returns setOf(mockkDoctor)
        }

        val doctorDaoImpl = DoctorDaoImpl(
            authorizer,
            doctorRepository,
            doctorPatientRepository,
            patientRepository,
            eventRepository,
            clinicRepository, sessionFactory
        )

        val result = runBlocking {
            doctorDaoImpl.getByClinic(GraphQLClinic.ID(clinicId), mockk())
        }

        assertEquals(setOf(mockkDoctor), result)
    }
}