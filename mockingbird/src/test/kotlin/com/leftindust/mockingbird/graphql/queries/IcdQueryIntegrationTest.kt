package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.ninjasquad.springmockk.MockkBean
import integration.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class IcdQueryIntegrationTest(
    @Autowired private val testClient: WebTestClient
) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun testIcdSearch() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
        }, mockk(relaxed = true))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=Graphql
                """query {
                    searchIcd(query: "aids", flatResults: true) {
                        destinationEntities {
                            code
                            id
                            title
                        }
                    }
                } """.trimMargin()
            )
            .exchange()
            .debugPrint()
            .verifyOnlyDataExists("searchIcd")
            .jsonPath("data.searchIcd.destinationEntities[0].code")
            .isNotEmpty
            .jsonPath("data.searchIcd.destinationEntities[0].title")
            .isNotEmpty
            .jsonPath("data.searchIcd.destinationEntities[0].id")
            .isNotEmpty
            .jsonPath("data.searchIcd.destinationEntities.length()")
            .value({assertTrue(it > 5)}, Int::class.java)
    }

    @Test
    internal fun testIcdSearchEmptyString() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
        }, mockk(relaxed = true))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=Graphql
                """query {
                    searchIcd(query: "") {
                        destinationEntities {
                            id
                        }
                    }
                }""".trimMargin()
            )
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("errors")
            .isNotEmpty
    }
}