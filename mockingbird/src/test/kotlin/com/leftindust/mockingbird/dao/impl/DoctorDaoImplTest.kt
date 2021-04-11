package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorPatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val doctorPatientRepository = mockk<HibernateDoctorPatientRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val visitRepository = mockk<HibernateVisitRepository>()

    @Test
    fun getByPatient() {
        val mockkDoctor = mockk<Doctor>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOne(any()) } returns mockk {
            every { id } returns 12
        }
        coEvery { doctorPatientRepository.getAllByPatientId(any<Long>()) } returns setOf(mockk {
            every { doctor } returns mockkDoctor
        })

        val doctorDaoImpl =
            DoctorDaoImpl(authorizer, doctorRepository, doctorPatientRepository, patientRepository, visitRepository)
        val actual = runBlocking { doctorDaoImpl.getByPatient(1000L, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkDoctor), actual)
    }

    @Test
    fun getByVisit() {
        val mockkDoctor = mockk<Doctor>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { visitRepository.getOne(any()) } returns mockk() {
            every { event } returns mockk() {
                every { doctors } returns setOf(mockkDoctor)
            }
        }

        val doctorDaoImpl =
            DoctorDaoImpl(authorizer, doctorRepository, doctorPatientRepository, patientRepository, visitRepository)
        val actual = runBlocking { doctorDaoImpl.getByVisit(1000L, mockk()) }.getOrThrow()

        assertEquals(setOf(mockkDoctor), actual)
    }

    @Test
    fun getByDoctor() {
        val mockkDoctor = mockk<Doctor>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getOne(any()) } returns mockkDoctor

        val doctorDaoImpl =
            DoctorDaoImpl(authorizer, doctorRepository, doctorPatientRepository, patientRepository, visitRepository)
        val actual = runBlocking { doctorDaoImpl.getByDoctor(1000L, mockk()) }.getOrThrow()

        assertEquals(mockkDoctor, actual)
    }

    @Test
    fun addDoctor() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val doctor = mockk<Doctor>()
        every { doctorRepository.save(any()) } returns doctor

        val doctorDaoImpl =
            DoctorDaoImpl(authorizer, doctorRepository, doctorPatientRepository, patientRepository, visitRepository)

        val graphQLDoctorInput = mockk<GraphQLDoctorInput>(relaxed = true)

        val result = runBlocking { doctorDaoImpl.addDoctor(graphQLDoctorInput, mockk()) }

        assertEquals(Success(doctor), result)
    }
}