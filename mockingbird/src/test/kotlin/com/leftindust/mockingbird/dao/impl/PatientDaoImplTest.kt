package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
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

    @Test
    fun getByPID() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(any()) } returns mockkPatient

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory
        )
        val actual = runBlocking { patientDaoImpl.getByPID(1000, mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)

    }

    @Test
    fun addNewPatient() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.save(mockkPatient) } returns mockkPatient

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val actual = runBlocking { patientDaoImpl.addNewPatient(mockkPatient, mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val actual = runBlocking { patientDaoImpl.removePatientByPID(1000L, mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val actual = runBlocking { patientDaoImpl.getByDoctor(1000L, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkPatient), actual)
    }

    @Test
    fun getByVisit() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { visitRepository.getOne(1000L) } returns mockk {
            every { patient } returns mockkPatient
        }

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val actual = runBlocking { patientDaoImpl.getByVisit(1000L, mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)
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
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val actual = runBlocking { patientDaoImpl.addDoctorToPatient(ID("1000"), ID("1001"), mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)
    }

    @Test
    fun update() {
        val mockkPatient = mockk<Patient> {
            every { setByGqlInput(any()) } returns Unit
        }
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(100) } returns mockkPatient

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory
        )

        val patientInput = mockk<GraphQLPatientInput> {
            every { pid } returns OptionalInput.Defined(gqlID(100))
        }

        val actual = runBlocking { patientDaoImpl.update(patientInput, mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)
    }
}