package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.DoctorPatient
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DoctorDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val doctorPatientRepository = mockk<HibernateDoctorPatientRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val eventRepository = mockk<HibernateEventRepository>()
    private val sessionFactory = mockk<SessionFactory>()


    @Test
    fun getByPatient() {
        val mockkDoctor = mockk<Doctor>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        val mockkPatient = mockk<Patient> {
            every { id } returns 12
        }
        every { patientRepository.getOne(any()) } returns mockkPatient
        val doctorPatient = mockk<DoctorPatient> {
            every { doctor } returns mockkDoctor
        }
        coEvery { doctorPatientRepository.getAllByPatientId(12L) } returns setOf(doctorPatient)


        val doctorDaoImpl = DoctorDaoImpl(
            authorizer, doctorRepository, doctorPatientRepository,
            patientRepository, eventRepository, sessionFactory
        )

        val actual = runBlocking { doctorDaoImpl.getByPatient(12, mockk()) }

        assertEquals(listOf(mockkDoctor), actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            patientRepository.getOne(12)
            mockkPatient.id
            doctorPatientRepository.getAllByPatientId(12L)
            doctorPatient.doctor
        }
    }

    @Test
    fun getByEvent() {
        val mockkDoctor = mockk<Doctor>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        val mockkEvent = mockk<Event> {
            every { doctors } returns setOf(mockkDoctor)
        }
        every { eventRepository.getOne(1000L) } returns mockkEvent

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                sessionFactory
            )
        val actual = runBlocking { doctorDaoImpl.getByEvent(1000L, mockk()) }

        assertEquals(setOf(mockkDoctor), actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            mockkEvent.doctors
            eventRepository.getOne(1000L)
        }
    }

    @Test
    fun getByDoctor() {
        val mockkDoctor = mockk<Doctor>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getOne(1000L) } returns mockkDoctor

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                sessionFactory
            )
        val actual = runBlocking { doctorDaoImpl.getByDoctor(1000L, mockk()) }

        assertEquals(mockkDoctor, actual)

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            doctorRepository.getOne(1000L)
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

        val graphQLDoctorInput = mockk<GraphQLDoctorEditInput>() {
            every { did } returns gqlID(1000)
        }

        val mockkDoctor = mockk<Doctor>() {
            every { setByGqlInput(graphQLDoctorInput, any()) } just runs
        }

        every { doctorRepository.getOne(1000) } returns mockkDoctor

        every { sessionFactory.currentSession } returns mockk()

        val doctorDaoImpl =
            DoctorDaoImpl(
                authorizer,
                doctorRepository,
                doctorPatientRepository,
                patientRepository,
                eventRepository,
                sessionFactory
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

        val graphQLDoctorInput = mockk<GraphQLDoctorEditInput>() {
            every { did } returns gqlID(1000)
        }

        val mockkDoctor = mockk<Doctor>() {
            every { setByGqlInput(graphQLDoctorInput, any()) } just runs
        }

        every { doctorRepository.getOne(1000) } returns mockkDoctor

        every { sessionFactory.currentSession } returns mockk()

        val doctorDaoImpl = DoctorDaoImpl(
            authorizer,
            doctorRepository,
            doctorPatientRepository,
            patientRepository,
            eventRepository,
            sessionFactory
        )

        assertThrows<NotAuthorizedException> {
            runBlocking { doctorDaoImpl.editDoctor(graphQLDoctorInput, mockk()) }
        }
    }
}