package com.leftindust.mockingbird.external.icd.impl

import com.leftindust.mockingbird.external.icd.IcdApiClientConfigBean
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class IcdFetcherImplTest {
    private val config = mockk<IcdApiClientConfigBean> {
        every { BASE_URL } returns "http://localhost:80/icd"
    }

    @Test
    fun linearization() {
        val icdFetcher = IcdFetcherImpl(config)
        assertDoesNotThrow {
            runBlocking { icdFetcher.linearization(linearizationName = "mms", FoundationIcdCode("1790791774")) }
        }
    }

    @Test
    fun linearizationSearch() {
        val icdFetcher = IcdFetcherImpl(config)
        assertDoesNotThrow {
            runBlocking {
                icdFetcher.linearizationSearch(
                    releaseId = GraphQLReleaseIdInput.R_2020_09,
                    linearizationName = "mms",
                    query = "covid",
                    flatResults = true
                )
            }
        }
    }

    @Test
    fun getDetails() {
        val icdFetcher = IcdFetcherImpl(config)
        assertDoesNotThrow {
            runBlocking { icdFetcher.getDetails(FoundationIcdCode("1790791774")) }
        }
    }

    @Test
    fun search() {
        val icdFetcher = IcdFetcherImpl(config)
        assertDoesNotThrow {
            runBlocking { icdFetcher.search("covid", flexiSearch = true, flatResults = false) }
        }
    }
}