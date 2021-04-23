package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Schedule
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.examples.GraphQLVisitExample
import com.leftindust.mockingbird.graphql.types.examples.StringFilter
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCodeInput
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
    private val eventRepository = mockk<HibernateEventRepository>()
    private val visitRepository = mockk<HibernateVisitRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()
    private val entityManager = mockk<EntityManager>()

    @Test
    fun getVisitByVid() {
        val mockkVisit = mockk<Visit>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { visitRepository.getOne(1000) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(
            authorizer,
            eventRepository,
            visitRepository,
            entityManager,
            patientRepository
        )

        val result = runBlocking { visitDaoImpl.getVisitByVid(1000, mockk()) }

        assertEquals(mockkVisit, result)
    }

    @Test
    fun addVisit() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getOne(1000) } returns mockk {
            every { id } returns 1000
            every { schedule } returns Schedule()
        }
        every { patientRepository.getOne(2000) } returns mockk {
            every { id } returns 2000
            every { schedule } returns Schedule()
        }

        every { eventRepository.getOne(4000) } returns mockk()

        val mockkVisit = mockk<Visit>()

        val visitDaoImpl = VisitDaoImpl(
            authorizer, eventRepository, visitRepository, entityManager,
            patientRepository
        )

        val visitInput = mockk<GraphQLVisitInput>(relaxed = true) {
            every { event } returns gqlID(4000)
            every { foundationIcdCode } returns FoundationIcdCodeInput("1222121")
        }

        every { visitRepository.save(any()) } returns mockkVisit

        val result = runBlocking { visitDaoImpl.addVisit(visitInput, mockk()) }

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
        val visitDaoImpl = VisitDaoImpl(authorizer, eventRepository, visitRepository, entityManager, patientRepository)
        val result = runBlocking {
            visitDaoImpl.getByExample(
                GraphQLVisitExample(vid = StringFilter(eq = "10000")),
                strict = true,
                mockk()
            )
        }
        assertEquals(listOf(mockkVisit), result)
    }
}