package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.extensions.timed
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class IcdQueryTest(@Autowired private val testClient: WebTestClient) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun testSearch() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
        }, mockk(relaxed = true))

        val query = "searchIcdLinearization"

        timed(query, repeat = 10) {
            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    """query { $query(query: "covid") { 
                |   words { 
                |       label 
                |       } 
                |   } 
                |} """.trimMargin()
                ).exchange()
        }.verifyOnlyDataExists(query)
    }
}