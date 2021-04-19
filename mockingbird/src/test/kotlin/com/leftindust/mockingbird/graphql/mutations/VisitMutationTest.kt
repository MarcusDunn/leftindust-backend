package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VisitMutationTest {
    private val visitDao = mockk<VisitDao>()
    private val graphQLAuthContext = mockk<GraphQLAuthContext>()

    @Test
    fun addVisit() {
        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val mockkVisit = mockk<Visit>(relaxed = true) {
            every { id } returns 1000L
        }

        coEvery { visitDao.addVisit(any(), any()) } returns mockkVisit

        val visitMutation = VisitMutation(visitDao)

        val visitMockk = mockk<GraphQLVisitInput>()

        val result = runBlocking { visitMutation.addVisit(visitMockk, graphQLAuthContext) }

        assertEquals(GraphQLVisit(mockkVisit, mockkVisit.id!!, graphQLAuthContext), result)
    }
}