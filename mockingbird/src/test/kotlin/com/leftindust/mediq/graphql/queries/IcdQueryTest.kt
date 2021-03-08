package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class IcdQueryTest(
    @Autowired private val icdQuery: IcdQuery
) {

    @Test
    fun searchIcd() {
        runBlocking {
            val result = icdQuery.searchIcd("Polio", authContext = FakeAuth.Valid.Context)
            assert(result.destinationEntities!!.isNotEmpty())
        }

    }

    @Test
    fun `searchIcd with empty string`() {
        assertThrows(GraphQLKotlinException::class.java) {
            runBlocking {
                icdQuery.searchIcd("", authContext = FakeAuth.Valid.Context)
            }
        }
    }

    @Test
    fun `searchIcd with no-result string`() {
        runBlocking {
            val result = icdQuery.searchIcd("WEEWOOWEEWOO", authContext = FakeAuth.Valid.Context)
            assert(result.destinationEntities!!.isEmpty()) { result.destinationEntities!! }
        }
    }

    @Test
    fun `findIcdDetails with invalid icd code`() {
        assertThrows(GraphQLKotlinException::class.java) {
            runBlocking {
                icdQuery.icd("WEE WOO WEE WOO", FakeAuth.Valid.Context)
            }
        }
    }

    @Test
    internal fun `test searchIcd returns at least one title`() {
        runBlocking {
            val result = icdQuery.searchIcd("Covid", authContext = FakeAuth.Valid.Context)
            assert(result.destinationEntities!!.any { it.title() != null })
        }
    }

    @Test
    fun icd() {
        runBlocking {
            val result = icdQuery.icd("1201727099", FakeAuth.Valid.Context)
            assert(result.title!!.value!!.contains("Narcolepsy")) { result.title!! }
        }
    }

    @Test
    fun searchIcdLinearization() {
        runBlocking {
            val result = icdQuery.searchIcdLinearization("aids", "2020-09", "mms", false, FakeAuth.Valid.Context)
            assert(result.destinationEntities!!.all { it.theCode != null })
        }
    }
}