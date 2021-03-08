package com.leftindust.mediq.dao

import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.external.icd.IcdFetcher
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestIcdFetcher {

    @Autowired
    lateinit var apiClient: IcdFetcher

    @Test
    internal fun `test getEntity 274880002`() {
        runBlocking {
            val entity = FoundationIcdCode("274880002")
            assert(apiClient.getDetails(entity).isSuccess())
        }
    }

    @Test
    internal fun `test getUri at entity-1359329403`() {
        runBlocking {
            val entity = FoundationIcdCode("1359329403")
            assert(apiClient.getDetails(entity).isSuccess())
        }
    }

    @Test
    internal fun `test search for AIDS`() {
        runBlocking {
            val query = "AIDS"
            assert(apiClient.search(query, flexiSearch = true, flatResults = false).isSuccess())
        }
    }

    @Test
    internal fun `test failing for invalid entity`() {
        runBlocking {
            val query = FoundationIcdCode("AIDS")
            assert(apiClient.getDetails(query).isFailure())
        }
    }
}
