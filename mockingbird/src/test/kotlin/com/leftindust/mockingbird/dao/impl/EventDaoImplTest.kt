package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.Success
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventDaoImplTest {
    private val hibernateEventRepository = mockk<HibernateEventRepository>()
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

        val eventDao = EventDaoImpl(hibernateEventRepository, authorizer)
        val event = EntityStore.graphQLEventInput("EventDaoImplTest.addEvent")
        val result = runBlocking { eventDao.addEvent(event, mockk()) }

        assertEquals(Success(eventEntity), result)
    }
}