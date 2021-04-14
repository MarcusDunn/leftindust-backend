package com.leftindust.mockingbird.dao.impl

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
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLTimeRangeInput
import integration.util.EntityStore
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventDaoImplTest {
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

        val eventEntity = mockk<Event>()
        every { hibernateEventRepository.save(any()) } returns eventEntity

        every { hibernatePatientRepository.getOne(any()) } returns mockk() {
            every { schedule } returns mockk() {
                every { addEvent(any()) } just runs
            }
        }

        every { hibernateDoctorRepository.getOne(any()) } returns mockk() {
            every { schedule } returns mockk() {
                every { addEvent(any()) } just runs
            }
        }

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )
        val event = EntityStore.graphQLEventInput("EventDaoImplTest.addEvent")
        val result = runBlocking { eventDao.addEvent(event, mockk()) }

        assertEquals(eventEntity, result.getOrNull())
    }

    @Test
    fun getMany() {
        coEvery {
            authorizer.getAuthorization(
                match { Action(Crud.READ to Tables.Event).isSuperset(it) },
                any()
            )
        } returns Authorization.Allowed

        val listOfEvent = (0 until 20).map {
            mockk<Event>(relaxed = true) {
                every { id } returns it.toLong()
            }
        }

        every { hibernateEventRepository.findAllByStartTimeAfterOrReoccurrenceIsNotNull(any()) } returns listOfEvent

        val eventDao = EventDaoImpl(
            hibernateEventRepository, hibernatePatientRepository,
            hibernateDoctorRepository, hibernateVisitRepository, authorizer
        )

        val start = mockk<GraphQLTimeInput> {
            every { before(any()) } returns true
            every { toTimestamp() } returns mockk()
        }
        val result = runBlocking { eventDao.getMany(GraphQLTimeRangeInput(start, mockk()), mockk()) }

        assertEquals(listOfEvent, result)
    }
}