package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLUtcTime
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import com.leftindust.mockingbird.util.EntityStore
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class EventDaoImplTestOld {
    private val hibernateEventRepository = mockk<HibernateEventRepository>()
    private val hibernatePatientRepository = mockk<HibernatePatientRepository>()
    private val hibernateDoctorRepository = mockk<HibernateDoctorRepository>()
    private val hibernateVisitRepository = mockk<HibernateVisitRepository>()
    private val authorizer = mockk<Authorizer>()

    @Test
    fun addEvent() {
        coEvery {
            authorizer.getAuthorization(
                match { Action(Crud.CREATE to Tables.Event).isSuperset(it) },
                any()
            )
        } returns Authorization.Allowed

        val mockkEvent = mockk<Event>()

        every { hibernateEventRepository.save(any()) } returns mockkEvent

        every { hibernatePatientRepository.findAllById(emptyList()) } returns emptyList()

        every { hibernateDoctorRepository.findAllById(emptyList()) } returns emptyList()

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )
        val event = EntityStore.graphQLEventInput("EventDaoImplTest.addEvent")
        val result = runBlocking { eventDao.addEvent(event, mockk()) }

        assertEquals(mockkEvent, result)
    }

    @Test
    fun getBetween() {
        coEvery {
            authorizer.getAuthorization(
                match { Action(Crud.READ to Tables.Event).isSuperset(it) },
                any()
            )
        } returns Authorization.Allowed

        val listOfMockkEvents = mockk<List<Event>>()

        every {
            hibernateEventRepository.getAllInRangeOrReoccurrenceIsNotNull(
                any(),
                any()
            )
        } returns listOfMockkEvents

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )

        val mockkStart = mockk<GraphQLUtcTime> {
            every { before(any()) } returns true
            every { toTimestamp() } returns mockk()
        }

        val mockkEnd = mockk<GraphQLUtcTime> {
            every { before(any()) } returns true
            every { toTimestamp() } returns mockk()
        }

        val result = runBlocking { eventDao.getBetween(GraphQLTimeRangeInput(mockkStart, mockkEnd), mockk()) }

        assertEquals(listOfMockkEvents, result)

    }

    @Test
    fun editEvent() {
        val eventID = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )

        val eventUpdateInput =
            GraphQLEventEditInput(GraphQLEvent.ID(eventID), description = OptionalInput.Defined("fancy descr"))

        val originalEventMockk = mockk<Event> {
            every { reoccurrence } returns null
            every { update(eventUpdateInput, null, null) } just runs
        }

        every { hibernateEventRepository.getById(eventID) } returns originalEventMockk

        every { hibernateEventRepository.delete(originalEventMockk) } just runs

        val result = runBlocking {
            eventDao.editEvent(
                eventUpdateInput,
                mockk()
            )
        }

        verify { originalEventMockk.update(eventUpdateInput, any(), any()) }

        assertEquals(originalEventMockk, result)
    }
}