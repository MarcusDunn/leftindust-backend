package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class VisitQueryTest {
    private val visitDao = mockk<VisitDao>()
    private val eventDao = mockk<EventDao>()

    private val graphQLAuthContext = mockk<GraphQLAuthContext>()

    @Test
    fun getVisitsByPatient() {
        val patientID = UUID.randomUUID()
        val eventID = UUID.randomUUID()
        val visitID = UUID.randomUUID()

        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val mockkEvent = mockk<Event>() {
            every { id } returns eventID
        }

        coEvery { eventDao.getByPatient(GraphQLPatient.ID(patientID), any()) } returns listOf(mockkEvent)

        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns visitID
        }

        coEvery { visitDao.getByEvent(GraphQLEvent.ID(eventID), any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)
        val result = runBlocking { visitQuery.visits(pid = GraphQLPatient.ID(patientID), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, graphQLAuthContext)), result)
    }

    @Test
    fun `get visits by doctor`() {
        val doctorID = UUID.randomUUID()
        val eventID = UUID.randomUUID()
        val visitID = UUID.randomUUID()

        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val mockkEvent = mockk<Event> {
            every { id } returns eventID
        }

        coEvery { eventDao.getByDoctor(GraphQLDoctor.ID(doctorID), any()) } returns listOf(mockkEvent)

        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns visitID
        }

        coEvery { visitDao.getByEvent(GraphQLEvent.ID(eventID), any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result = runBlocking { visitQuery.visits(did = GraphQLDoctor.ID(doctorID), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, graphQLAuthContext)), result)

    }

    @Test
    fun `get visit by vids`() {
        val visitID = UUID.randomUUID()

        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns visitID
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitByVid(GraphQLVisit.ID(visitID), any()) } returns mockkVisit

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result =
            runBlocking { visitQuery.visits(vids = listOf(GraphQLVisit.ID(visitID)), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, graphQLAuthContext)), result)
    }

    @Test
    fun `get visit by pid and did`() {
        val visitID1 = UUID.randomUUID()
        val visitID2 = UUID.randomUUID()
        val visitID3 = UUID.randomUUID()
        val eventID1 = UUID.randomUUID()
        val eventID2 = UUID.randomUUID()
        val eventID3 = UUID.randomUUID()
        val patientID = UUID.randomUUID()
        val doctorID = UUID.randomUUID()

        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit1 = mockk<Visit>(relaxed = true) {
            every { id } returns visitID1
        }
        val mockkVisit2 = mockk<Visit>(relaxed = true) {
            every { id } returns visitID2
        }
        val mockkVisit3 = mockk<Visit>(relaxed = true) {
            every { id } returns visitID3
        }

        val mockkEvent1 = mockk<Event>() {
            every { id } returns eventID1
        }

        val mockkEvent2 = mockk<Event>() {
            every { id } returns eventID2
        }

        val mockkEvent3 = mockk<Event>() {
            every { id } returns eventID3
        }

        coEvery { eventDao.getByPatient(GraphQLPatient.ID(patientID), any()) } returns listOf(mockkEvent1, mockkEvent3)

        coEvery { visitDao.getByEvent(GraphQLEvent.ID(eventID1), any()) } returns mockkVisit1

        coEvery { eventDao.getByDoctor(GraphQLDoctor.ID(doctorID), any()) } returns listOf(mockkEvent2)

        coEvery { visitDao.getByEvent(GraphQLEvent.ID(eventID2), any()) } returns mockkVisit2

        coEvery { visitDao.getByEvent(GraphQLEvent.ID(eventID3), any()) } returns mockkVisit3

        val visitQuery = VisitQuery(visitDao, eventDao)

        val result = runBlocking {
            visitQuery.visits(
                did = GraphQLDoctor.ID(doctorID),
                pid = GraphQLPatient.ID(patientID),
                graphQLAuthContext = graphQLAuthContext
            )
        }

        assertEquals(
            listOf(mockkVisit1, mockkVisit3, mockkVisit2)
                .map { GraphQLVisit(it, graphQLAuthContext) },
            result
        )
    }
}