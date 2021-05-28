package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.Authorization
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

    @MockkBean
    private lateinit var authorizer: Authorizer

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
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val event = hibernateEventRepository.save(EntityStore.event("EventQueryTest.test get events by time range"))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """query{ events(range: {
                |   start: {unixMilliseconds: ${event.startTime.toInstant().toEpochMilli() - 1}},
                |   end: {unixMilliseconds: ${event.endTime!!.toInstant().toEpochMilli() + 1}}
                |   }) {
                |       eid {
                |        id
                |       }
                |       allDay
                |       description
                |       endTime {unixMilliseconds}
                |       doctors {
                |        did {id}
                |        firstName
                |        lastName
                |        title
                |       }
                |       patients {
                |        pid {id}
                |        firstName
                |        lastName
                |        dateOfBirth {
                |            day
                |            month
                |            year
                |        }
                |        sex
                |       }
                |       startTime {unixMilliseconds}
                |       title
                |   }
                |}
                |""".trimMargin().also { println(it) }
            )
            .exchange()
            .debugPrint()
            .verifyOnlyDataExists("events")
            .jsonPath("$DATA_JSON_PATH.events[0].eid.id")
            .isEqualTo(event.id!!.toString())

        hibernateEventRepository.delete(event)
    }
}

