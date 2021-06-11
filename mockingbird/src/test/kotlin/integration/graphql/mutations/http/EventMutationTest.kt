package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.verifyOnlyDataExists
import io.mockk.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.sql.Timestamp
import java.util.*
import kotlin.random.Random

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient(timeout = "P2DT3H4M")
@Tag("Integration")
class EventMutationTest(
    @Autowired private val testClient: WebTestClient,
) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @MockkBean
    private lateinit var eventDao: EventDao

    @Test
    internal fun testAddEvent() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patientUUID = UUID.nameUUIDFromBytes(Random(100).nextBytes(20))

        val start = Timestamp.valueOf("2018-09-01 09:01:15")
        val end = Timestamp.valueOf("2018-09-01 10:01:15")
        val slot = slot<GraphQLEventInput>()
        coEvery { eventDao.addEvent(capture(slot), any()) } answers { Event(slot.captured, emptySet(), emptySet()).apply { this.id = UUID.randomUUID() } }

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """
                |mutation { addEvent(event: {
                |    title: "MY EVENT",
                |    description: "YO YO YO this do be an event doe",
                |    allDay: false,
                |    start: {unixMilliseconds: ${start.time}},
                |    end: {unixMilliseconds: ${end.time}},
                |    patients: [{id: "$patientUUID"}]
                |}) {
                |   eid {
                |       id
                |   }
                |   title
                |   }
                |}""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addEvent")
            .jsonPath("data.addEvent.title")
            .isEqualTo("MY EVENT")
    }
}