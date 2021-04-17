package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import integration.util.EntityStore
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventDaoImplTest {
    private val hibernateEventRepository = mockk<HibernateEventRepository>()
    private val hibernatePatientRepository = mockk<HibernatePatientRepository>()
    private val hibernateDoctorRepository = mockk<HibernateDoctorRepository>()
    private val hibernateVisitRepository = mockk<HibernateVisitRepository>()
    private val authorizer = mockk<Authorizer>()

    @AfterEach
    internal fun tearDown() {
        confirmVerified(
            hibernateEventRepository,
            hibernatePatientRepository,
            hibernateDoctorRepository,
            hibernateVisitRepository,
            authorizer
        )
    }

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

        val mockkPatient = mockk<Patient>()

        every { hibernatePatientRepository.getOne(any()) } returns mockkPatient

        val mockkDoctor = mockk<Doctor>()

        every { hibernateDoctorRepository.getOne(any()) } returns mockkDoctor

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )
        val event = EntityStore.graphQLEventInput("EventDaoImplTest.addEvent")
        val result = runBlocking { eventDao.addEvent(event, mockk()) }

        coVerifyAll {
            hibernateEventRepository.save(any())
            authorizer.getAuthorization(any(), any())
        }

        confirmVerified(mockkEvent, mockkDoctor, mockkPatient)

        assertEquals(mockkEvent, result.getOrNull())
    }

    @Test
    fun getMany() {
        coEvery {
            authorizer.getAuthorization(
                match { Action(Crud.READ to Tables.Event).isSuperset(it) },
                any()
            )
        } returns Authorization.Allowed

        val listOfMockkEvents = mockk<List<Event>>()

        every { hibernateEventRepository.findAllByStartTimeAfterOrReoccurrenceIsNotNull(any()) } returns listOfMockkEvents

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )

        val mockkStart = mockk<GraphQLTimeInput> {
            every { before(any()) } returns true
            every { toTimestamp() } returns mockk()
        }

        val result = runBlocking { eventDao.getMany(GraphQLTimeRangeInput(mockkStart, mockk()), mockk()) }


        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            mockkStart.before(any())
            mockkStart.toTimestamp()
            hibernateEventRepository.findAllByStartTimeAfterOrReoccurrenceIsNotNull(any())
        }

        confirmVerified(mockkStart, listOfMockkEvents)

        assertEquals(listOfMockkEvents, result)

    }

    @Test
    fun editEvent() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )

        val eventUpdateInput = GraphQLEventEditInput(gqlID(1000), description = OptionalInput.Defined("fancy descr"))

        val updateEventMockk = mockk<Event>()

        val originalEventMockk = mockk<Event>() {
            every { update(eventUpdateInput, null, null) } returns updateEventMockk
        }

        every { hibernateEventRepository.getOne(1000) } returns originalEventMockk

        every { hibernateEventRepository.save(updateEventMockk) } returns updateEventMockk

        every { hibernateEventRepository.delete(originalEventMockk) } just runs

        val result = runBlocking {
            eventDao.editEvent(
                eventUpdateInput,
                mockk()
            )
        }

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            originalEventMockk.update(eventUpdateInput, null, null)
            hibernateEventRepository.getOne(1000)
            hibernateEventRepository.save(updateEventMockk)
            hibernateEventRepository.delete(originalEventMockk)
        }

        confirmVerified(updateEventMockk, originalEventMockk)

        assertEquals(updateEventMockk, result)
    }
}