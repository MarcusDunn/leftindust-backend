package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VisitDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val visitRepository = mockk<HibernateVisitRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()


    @Test
    fun getVisitsForPatientPid() {
        val mockkVisit = mockk<Visit>()

        every { visitRepository.getAllByPatientId(1000) } returns listOf(mockkVisit)

        every { patientRepository.getOne(1000) } returns mockk()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository)

        val result = runBlocking { visitDaoImpl.getVisitsForPatientPid(1000, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkVisit), result)
    }

    @Test
    fun getVisitByVid() {
        val mockkVisit = mockk<Visit>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { visitRepository.getOne(1000) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository)

        val result = runBlocking { visitDaoImpl.getVisitByVid(1000, mockk()) }.getOrThrow()

        assertEquals(mockkVisit, result)
    }

    @Test
    fun getVisitsByDoctor() {
        val mockkVisit = mockk<Visit>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { doctorRepository.getOne(1000) } returns mockk()

        every { visitRepository.getAllByDoctorId(1000L) } returns listOf(mockkVisit)

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository)

        val result = runBlocking { visitDaoImpl.getVisitsByDoctor(1000, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkVisit), result)
    }

    @Test
    fun addVisit() {
        val mockkVisit = mockk<Visit>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { doctorRepository.getOne(1000) } returns mockk()
        every { patientRepository.getOne(2000) } returns mockk()
        every { visitRepository.save(any()) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository)

        val visitInput = mockk<GraphQLVisitInput>(relaxed = true) {
            every { doctorId } returns gqlID(1000)
            every { patientId } returns gqlID(2000)
        }

        val result = runBlocking { visitDaoImpl.addVisit(visitInput, mockk()) }.getOrThrow()

        assertEquals(mockkVisit, result)
    }
}