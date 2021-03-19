package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VisitQueryTest {
    private val visitDao = mockk<VisitDao>()
    private val graphQLAuthContext = mockk<GraphQLAuthContext>()

    @Test
    fun getVisitsByPatient() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 2000
        }
        coEvery { visitDao.getVisitsForPatientPid(1000, any()) } returns Success(listOf(mockkVisit))
        val visitQuery = VisitQuery(visitDao)
        val result = runBlocking { visitQuery.visits(pid = gqlID(1000), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)
    }

    @Test
    fun `get visits by doctor`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitsByDoctor(2000, any()) } returns Success(listOf(mockkVisit))

        val visitQuery = VisitQuery(visitDao)

        val result = runBlocking { visitQuery.visits(did = gqlID(2000), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)
    }

    @Test
    fun `get visit by vids`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitByVid(3000, any()) } returns Success(mockkVisit)

        val visitQuery = VisitQuery(visitDao)

        val result =
            runBlocking { visitQuery.visits(vids = listOf(gqlID(3000)), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)
    }

    @Test
    fun `get visit by pid and dic strict`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit1 = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
            every { doctor } returns mockk {
                every { id } returns 1000
            }
        }
        val mockkVisit2 = mockk<Visit>(relaxed = true) {
            every { id } returns 2000
            every { doctor } returns mockk {
                every { id } returns 3000
            }
        }
        val mockkVisit3 = mockk<Visit>(relaxed = true) {
            every { id } returns 3000
            every { doctor } returns mockk {
                every { id } returns 1000
            }
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitsForPatientPid(2000, any()) } returns Success(
            listOf(
                mockkVisit1,
                mockkVisit2,
                mockkVisit3
            )
        )

        val visitQuery = VisitQuery(visitDao)

        val result = runBlocking {
            visitQuery.visits(
                did = gqlID(3000),
                pid = gqlID(2000),
                graphQLAuthContext = graphQLAuthContext
            )
        }

        assertEquals(listOf(GraphQLVisit(mockkVisit2, mockkVisit2.id!!, graphQLAuthContext)), result)

        coVerify { visitDao.getVisitsForPatientPid(any(), any()) }
    }

    @Test
    fun `get visit by pid and dic not strict`() {
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
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitsForPatientPid(2000, any()) } returns Success(
            listOf(
                mockkVisit1,
            )
        )
        coEvery { visitDao.getVisitsByDoctor(3000, any()) } returns Success(
            listOf(
                mockkVisit1,
                mockkVisit2,
                mockkVisit3,
            )
        )

        val visitQuery = VisitQuery(visitDao)

        val result = runBlocking {
            visitQuery.visits(
                did = gqlID(3000),
                pid = gqlID(2000),
                strict = false,
                graphQLAuthContext = graphQLAuthContext
            )
        }
        assertEquals(
            listOf(mockkVisit1, mockkVisit2, mockkVisit3)
                .map { GraphQLVisit(it, it.id!!, graphQLAuthContext) },
            result
        )

        coVerify { visitDao.getVisitsByDoctor(any(), any()) }
        coVerify { visitDao.getVisitsForPatientPid(any(), any()) }

    }

    @Test
    fun `search visits by example`() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getByExample(any(), any(), any()) } returns Success(listOf(mockkVisit))

        val visitQuery = VisitQuery(visitDao)

        val result = runBlocking { visitQuery.visits(example = mockk(), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)

        coVerify { visitDao.getByExample(any(), any(), any()) }
    }
}