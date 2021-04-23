package com.leftindust.mockingbird.external.icd.impl

import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.input.GraphQLReleaseIdInput
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class IcdFetcherImplTest {

    @Test
    fun getLinearizationEntity() {
        val icdFetcher = IcdFetcherImpl(mockk())
        runBlocking {
            icdFetcher.getLinearizationEntity(
                GraphQLReleaseIdInput.R_2020_09,
                FoundationIcdCode("1790791774")
            )
        }
    }

    @Test
    fun linearization() {
        val icdFetcher = IcdFetcherImpl(mockk())
        runBlocking { icdFetcher.linearization(linearizationName = "mms", FoundationIcdCode("1790791774")) }
    }

    @Test
    fun linearizationSearch() {
        val icdFetcher = IcdFetcherImpl(mockk())
        runBlocking {
            icdFetcher.linearizationSearch(
                releaseId = GraphQLReleaseIdInput.R_2020_09,
                linearizationName = "mms",
                query = "covid",
                flatResults = true
            )
        }
    }

    @Test
    fun getDetails() {
        val icdFetcher = IcdFetcherImpl(mockk())
        runBlocking { icdFetcher.getDetails(FoundationIcdCode("1790791774")) }
    }

    @Test
    fun search() {
        val icdFetcher = IcdFetcherImpl(mockk())
        runBlocking { icdFetcher.search("covid", flexiSearch = true, flatResults = false) }
    }
}