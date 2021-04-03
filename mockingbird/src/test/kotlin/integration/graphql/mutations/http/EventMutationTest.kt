package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
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
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@AutoConfigureWebTestClient
@Tag("Integration")
class EventMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun testAddEvent() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
            every { uid } returns "admin"
        }, mockk(relaxed = true))

        val mutation = "addEvent"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """
                |mutation { $mutation(event: {
                    |title: "MY EVENT",
                    |description: "YO YO YO this do be an event doe",
                    |start: {time: {unixMilliseconds: ${Timestamp.valueOf("2018-09-01 09:01:15").time}}},
                    |end: {time: {unixMilliseconds: ${Timestamp.valueOf("2018-09-01 10:01:15").time}}}
                |}) {
                |   title
                |   }
                |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists(mutation)

        hibernateEventRepository.findAll().find { it.title == "MY EVENT" } ?: fail("did not persist")
    }
}