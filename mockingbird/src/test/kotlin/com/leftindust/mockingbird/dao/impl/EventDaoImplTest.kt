package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import integration.util.EntityStore
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventDaoImplTest {
    private val hibernateEventRepository = mockk<HibernateEventRepository>()
    private val authorizer = mockk<Authorizer>()

    @Test
    fun addEvent() {
        val eventDao = EventDaoImpl(hibernateEventRepository, authorizer)

        val event = EntityStore.graphQLEventInput("EventDaoImplTest.addEvent")
        val result = eventDao.addEvent(event, mockk())

        val expected = null

        assertEquals(expected, result)
    }
}