package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCodeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import javax.persistence.EntityManager

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

        val visitID = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        every { visitRepository.getById(visitID) } returns mockkVisit

        val visitDaoImpl = VisitDaoImpl(
            authorizer,
            eventRepository,
            visitRepository,
            entityManager,
            patientRepository
        )

        val result = runBlocking { visitDaoImpl.getVisitByVid(GraphQLVisit.ID(visitID), mockk()) }

        assertEquals(mockkVisit, result)
    }

    @Test
    fun addVisit() {
        val doctorID = UUID.randomUUID()
        val patientID = UUID.randomUUID()
        val eventID = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { doctorRepository.getById(doctorID) } returns mockk {
            every { id } returns doctorID
            every { events } returns mutableSetOf()
        }
        every { patientRepository.getById(patientID) } returns mockk {
            every { id } returns patientID
            every { events } returns mutableSetOf()
        }

        every { eventRepository.getById(eventID) } returns mockk()

        val mockkVisit = mockk<Visit>()

        val visitDaoImpl = VisitDaoImpl(
            authorizer, eventRepository, visitRepository, entityManager,
            patientRepository
        )

        val visitInput = mockk<GraphQLVisitInput>(relaxed = true) {
            every { eid } returns GraphQLEvent.ID(eventID)
            every { foundationIcdCode } returns FoundationIcdCodeInput("1222121")
        }

        every { visitRepository.save(any()) } returns mockkVisit

        val result = runBlocking { visitDaoImpl.addVisit(visitInput, mockk()) }

        assertEquals(mockkVisit, result)
    }
}