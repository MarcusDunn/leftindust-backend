package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.ninjasquad.springmockk.MockkBean
import integration.*
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class EventQueryTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernateEventRepository: HibernateEventRepository
) {

    val count = hibernateEventRepository.count()

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @BeforeEach
    internal fun setUp() {
        assert(hibernateEventRepository.count() == count) { "leaked events in EventQueryTest" }
    }

    @AfterEach
    internal fun tearDown() {
        assert(hibernateEventRepository.count() == count) { "leaked events in EventQueryTest" }
    }

    @Test
    internal fun `test get events by time range`() {
        coEvery { contextFactory.generateContext(any()) } returns mockk() {
            every { getHTTPRequestHeader(any()) } returns "yeet"
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val query = "events"

        val event = hibernateEventRepository.save(EntityStore.event("EventQueryTest.test get events by time range"))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """query{
                |   $query(range: {start: {unixMilliseconds: ${
                    event.startTime.toInstant().toEpochMilli() - 1000
                }}, end: {unixMilliseconds: ${event.endTime!!.toInstant().toEpochMilli() + 1000}}}) {
                |       eid
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists(query)
            .jsonPath("$DATA_JSON_PATH.$query[0].eid")
            .isEqualTo(event.id!!)

        hibernateEventRepository.delete(event)
    }
}

