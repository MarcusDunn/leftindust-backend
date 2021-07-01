package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
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
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
@Transactional
class UserQuery(
    @Autowired private val testClient: WebTestClient,
) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun getUser() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
            every { uid } returns "lf2fnZU6eEVHmeA9f2J68cmZrl72"
        }, mockk(relaxed = true))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                // language=GraphQL
                """query {
                |   user(uid:"lf2fnZU6eEVHmeA9f2J68cmZrl72"){
                |      isRegistered
                |      group{
                |       name
                |      }
                |   }
                |}""".trimMargin()
            ).exchange()
            .verifyOnlyDataExists("user")
    }
}