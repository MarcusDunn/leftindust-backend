package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import io.mockk.coEvery
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
            every { id } returns 2000
        }
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        coEvery { visitDao.getVisitsByDoctor(2000, any()) } returns Success(listOf(mockkVisit))

        val visitQuery = VisitQuery(visitDao)

        val result = runBlocking { visitQuery.visits(did = gqlID(2000), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(listOf(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext)), result)
    }
}