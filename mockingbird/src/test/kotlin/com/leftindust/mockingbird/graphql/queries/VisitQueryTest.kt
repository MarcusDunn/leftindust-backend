package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VisitQueryTest {
    private val visitDao = mockk<VisitDao>()
    private val eventDao = mockk<EventDao>()

    private val graphQLAuthContext = mockk<GraphQLAuthContext>()

    @AfterEach
    internal fun tearDown() {
        confirmVerified(visitDao, eventDao)
    }

    @Test
    fun getVisitsByPatient() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val mockkEvent = mockk<Event>() {
            every { id } returns 4000
        }

        coEvery { eventDao.getByPatient(1000L, any()) } returns listOf(mockkEvent)

        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 2000
        }

        coEvery { visitDao.getByEvent(4000L, any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)
        val result = runBlocking { visitQuery.visits(pid = gqlID(1000), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)

        coVerifyAll {
            graphQLAuthContext.mediqAuthToken
            visitDao.getByEvent(4000L, any())
            eventDao.getByPatient(1000L, any())
            mockkEvent.id
            mockkVisit.id
            mockkVisit.title
            mockkVisit.icdFoundationCode
            mockkVisit.description
        }

        confirmVerified(mockkEvent, mockkVisit)
    }

    @Test
    fun `get visits by doctor`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val mockkEvent = mockk<Event> {
            every { id } returns 4000
        }

        coEvery { eventDao.getByDoctor(2000L, any()) } returns listOf(mockkEvent)

        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }

        coEvery { visitDao.getByEvent(4000L, any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result = runBlocking { visitQuery.visits(did = gqlID(2000), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)

        coVerifyAll {
            graphQLAuthContext.mediqAuthToken
            eventDao.getByDoctor(2000L, any())
            visitDao.getByEvent(4000L, any())
            mockkEvent.id
            mockkVisit.id
            mockkVisit.title
            mockkVisit.icdFoundationCode
            mockkVisit.description
        }

        confirmVerified(mockkEvent, mockkVisit)
    }

    @Test
    fun `get visit by vids`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitByVid(3000, any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result =
            runBlocking { visitQuery.visits(vids = listOf(gqlID(3000)), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)

        coVerifyAll {
            graphQLAuthContext.mediqAuthToken
            visitDao.getVisitByVid(3000, any())
            mockkVisit.id
            mockkVisit.title
            mockkVisit.icdFoundationCode
            mockkVisit.description
        }
    }

    @Test
    fun `get visit by pid and did`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit1 = mockk<Visit>(relaxed = true) {
            every { id } returns 8001
        }
        val mockkVisit2 = mockk<Visit>(relaxed = true) {
            every { id } returns 8000
        }
        val mockkVisit3 = mockk<Visit>(relaxed = true) {
            every { id } returns 8002
        }

        val mockkEvent1 = mockk<Event>() {
            every { id } returns 4000
        }

        val mockkEvent2 = mockk<Event>() {
            every { id } returns 4001
        }

        val mockkEvent3 = mockk<Event>() {
            every { id } returns 4002
        }

        coEvery { eventDao.getByPatient(2000L, any()) } returns listOf(mockkEvent1, mockkEvent3)

        coEvery { visitDao.getByEvent(4000, any()) } returns mockkVisit1

        coEvery { eventDao.getByDoctor(3000L, any()) } returns listOf(mockkEvent2)

        coEvery { visitDao.getByEvent(4001, any()) } returns mockkVisit2

        coEvery { visitDao.getByEvent(4002, any()) } returns mockkVisit3

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result = runBlocking {
            visitQuery.visits(
                did = gqlID(3000),
                pid = gqlID(2000),
                graphQLAuthContext = graphQLAuthContext
            )
        }

        assertEquals(
            listOf(mockkVisit1, mockkVisit3, mockkVisit2)
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) },
            result
        )

        coVerifyAll {
            graphQLAuthContext.mediqAuthToken
            visitDao.getByEvent(any(), any())
            eventDao.getByDoctor(3000L, any())
            eventDao.getByPatient(2000L, any())
        }
    }

    @Test
    fun `search visits by example`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getByExample(any(), any(), any()) } returns listOf(mockkVisit)

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result = runBlocking { visitQuery.visits(example = mockk(), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)

        coVerifyAll {
            graphQLAuthContext.mediqAuthToken
            mockkVisit.id
            mockkVisit.title
            mockkVisit.description
            mockkVisit.icdFoundationCode
            visitDao.getByExample(any(), any(), any())
        }
    }
}