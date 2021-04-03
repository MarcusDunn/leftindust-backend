package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample
import com.leftindust.mockingbird.graphql.types.examples.StringFilter
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaQuery

internal class VisitDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val visitRepository = mockk<HibernateVisitRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val entityManager = mockk<EntityManager>()


    @Test
    fun getVisitsForPatientPid() {
        val mockkVisit = mockk<Visit>()

        every { visitRepository.getAllByPatientId(1000) } returns listOf(mockkVisit)

        every { patientRepository.getOne(1000) } returns mockk()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository, entityManager)

        val result = runBlocking { visitDaoImpl.getVisitsForPatientPid(1000, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkVisit), result)
    }

    @Test
    fun getVisitByVid() {
        val mockkVisit = mockk<Visit>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { visitRepository.getOne(1000) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository, entityManager)

        val result = runBlocking { visitDaoImpl.getVisitByVid(1000, mockk()) }.getOrThrow()

        assertEquals(mockkVisit, result)
    }

    @Test
    fun getVisitsByDoctor() {
        val mockkVisit = mockk<Visit>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { doctorRepository.getOne(1000) } returns mockk()

        every { visitRepository.getAllByDoctorId(1000L) } returns listOf(mockkVisit)

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository, entityManager)

        val result = runBlocking { visitDaoImpl.getVisitsByDoctor(1000, mockk()) }.getOrThrow()

        assertEquals(listOf(mockkVisit), result)
    }

    @Test
    fun addVisit() {
        val mockkVisit = mockk<Visit>()
        val mockkEvent = mockk<Event>() {
            every { id } returns 4000L
        }
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { doctorRepository.getOne(1000) } returns mockk() {
            every { schedule } returns mockk {
                every { events } returns setOf(mockkEvent)
            }
        }
        every { patientRepository.getOne(2000) } returns mockk()
        every { visitRepository.save(any()) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository, entityManager)

        val visitInput = mockk<GraphQLVisitInput>(relaxed = true) {
            every { doctor } returns gqlID(1000)
            every { patient } returns gqlID(2000)
            every { event } returns gqlID(4000)
        }

        val result = runBlocking { visitDaoImpl.addVisit(visitInput, mockk()) }.getOrThrow()

        assertEquals(mockkVisit, result)
    }

    @Test
    fun getByExample() {
        val mockkVisit = mockk<Visit>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { entityManager.createQuery(any<CriteriaQuery<Visit>>()) } returns mockk {
            every { resultList } returns listOf(mockkVisit)
        }
        every { entityManager.criteriaBuilder } returns mockk {
            every { and(*anyVararg()) } returns mockk()
            every { equal(any(), "10000") } returns mockk()
            every { createQuery(Visit::class.java) } returns mockk {
                every { from(Visit::class.java) } returns mockk(relaxed = true)
                every { select(any()) } returns mockk {
                    every { where(*anyVararg()) } returns mockk()
                }
            }
        }
        val visitDaoImpl = VisitDaoImpl(authorizer, visitRepository, doctorRepository, patientRepository, entityManager)
        val result = runBlocking {
            visitDaoImpl.getByExample(
                GraphQLVisitExample(vid = StringFilter(eq = "10000")),
                strict = true,
                mockk()
            )
        }.getOrThrow()
        assertEquals(listOf(mockkVisit), result)
    }
}