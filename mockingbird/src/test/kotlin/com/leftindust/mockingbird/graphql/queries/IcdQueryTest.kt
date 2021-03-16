package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.external.icd.IcdFetcher
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdFoundationEntity
import com.leftindust.mockingbird.graphql.types.icd.GraphQLIcdSearchResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IcdQueryTest {
    private val client = mockk<IcdFetcher>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun searchIcd() {
        val mockkIcdSearchResult = mockk<GraphQLIcdSearchResult>()

        coEvery { client.search(any(), any(), any()) } returns mockk {
            every { getOrThrow() } returns mockkIcdSearchResult
        }

        every { authContext.mediqAuthToken } returns mockk {
            every { isVerified() } returns true
        }

        val icdQuery = IcdQuery(client)

        val result = runBlocking { icdQuery.searchIcd("hello!", authContext = authContext) }

        assertEquals(mockkIcdSearchResult, result)
    }

    @Test
    fun searchIcdLinearization() {
        val mockkIcdSearchResult = mockk<GraphQLIcdSearchResult>()

        coEvery { client.linearizationSearch(any(), any(), any(), any())} returns mockk() {
            every { getOrThrow() } returns mockkIcdSearchResult
        }

        val icdQuery = IcdQuery(client)

        every { authContext.mediqAuthToken } returns mockk {
            every { isVerified() } returns true
        }

        val result = runBlocking { icdQuery.searchIcdLinearization("hello!", authContext = authContext) }

        assertEquals(mockkIcdSearchResult, result)
    }

    @Test
    fun icd() {
        val mockkIcdFoundationEntity = mockk<GraphQLIcdFoundationEntity>()
        val icdQuery = IcdQuery(client)

        coEvery { client.getDetails(any()) } returns mockk {
            every { getOrThrow() } returns mockkIcdFoundationEntity
        }

        every { authContext.mediqAuthToken } returns mockk {
            every { isVerified() } returns true
        }

        val result = runBlocking { icdQuery.icd("hello!", authContext = authContext) }

        assertEquals(mockkIcdFoundationEntity, result)
    }
}