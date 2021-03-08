package com.leftindust.mediq.external.icd.impl

import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.external.icd.IcdFetcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class IcdFetcherTest(
    @Autowired private val icdFetcher: IcdFetcher
) {

    @Test
    fun search() {
        runBlocking {
            val result = icdFetcher.search("Sleep", flexiSearch = true, flatResults = false)
            assert(result.isSuccess())
        }
    }

    @Test
    fun `search with empty string`() {
        runBlocking {
            val result = icdFetcher.search("", flexiSearch = false, flatResults = false)
            assert(result is Failure)
        }
    }

    @Test
    fun getDetails() {
        runBlocking {
            val result = icdFetcher.getDetails(FoundationIcdCode("1201727099"))
            assert(result.getOrThrow().title!!.value!!.contains("Narcolepsy"))
        }
    }

    @Test
    fun `getDetails with invalid ICD code`() {
        runBlocking {
            val result = icdFetcher.getDetails(FoundationIcdCode("WEE WOO WEE WOO"))
            assert(result.isFailure())
        }
    }
}