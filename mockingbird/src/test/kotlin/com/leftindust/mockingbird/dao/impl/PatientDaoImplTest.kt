package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import javax.persistence.EntityManager

internal class PatientDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val doctorPatientRepository = mockk<HibernateDoctorPatientRepository>()
    private val visitRepository = mockk<HibernateVisitRepository>()
    private val sessionFactory = mockk<SessionFactory>()
    private val entityManager = mockk<EntityManager>()

    @Test
    fun getByPID() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(any()) } returns mockkPatient

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )
        val actual = runBlocking { patientDaoImpl.getByPID(1000, mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)

    }

    @Test
    fun addNewPatient() {
        val graphQLPatientInput = GraphQLPatientInput(
            firstName = OptionalInput.Defined("hello"),
            lastName = OptionalInput.Defined("world"),
            dateOfBirth = OptionalInput.Defined(GraphQLTimeInput(Timestamp.valueOf("2020-01-02 09:01:15"))),
            sex = OptionalInput.Defined(Sex.Male)
        )
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.save(any()) } returns mockkPatient
        every { sessionFactory.currentSession } returns mockk()

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val actual = runBlocking { patientDaoImpl.addNewPatient(graphQLPatientInput, mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val actual = runBlocking { patientDaoImpl.removeByPID(1000L, mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val actual = runBlocking { patientDaoImpl.getByDoctor(1000L, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkPatient), actual)
    }

    @Test
    fun getByVisit() {
        val mockkPatient = mockk<Patient>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { visitRepository.getOne(1000L) } returns mockk() {
            every { event } returns mockk() {
                every { patients } returns setOf(mockkPatient)
            }
        }

        val patientDaoImpl = PatientDaoImpl(
            authorizer, patientRepository, doctorRepository,
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val actual = runBlocking { patientDaoImpl.getByVisit(1000L, mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val actual = runBlocking { patientDaoImpl.addDoctorToPatient(ID("1000"), ID("1001"), mockk()) }.getOrThrow()

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
            doctorPatientRepository, visitRepository, sessionFactory,
            entityManager
        )

        val patientInput = mockk<GraphQLPatientInput> {
            every { pid } returns OptionalInput.Defined(gqlID(100))
        }

        val actual = runBlocking { patientDaoImpl.update(patientInput, mockk()) }.getOrThrow()

        assertEquals(mockkPatient, actual)
    }
}