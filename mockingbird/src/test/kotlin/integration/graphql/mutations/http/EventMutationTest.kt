package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import integration.*
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class EventMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val sessionFactory: SessionFactory,
) {
    val eventCount = hibernateEventRepository.count()
    val patientCount = hibernatePatientRepository.count()

    @BeforeEach
    @AfterEach
    internal fun countRemainsUnchanged() {
        assert(eventCount == hibernateEventRepository.count()) {"leaked events in EventMutationTest"}
        assert(patientCount == hibernateEventRepository.count()) {"leaked patients in EventMutationTest"}
    }

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun testAddEvent() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patient = hibernatePatientRepository.save(EntityStore.patient("EventMutationTest.testAddEvent"))

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
                |    start: {unixMilliseconds: ${Timestamp.valueOf("2018-09-01 09:01:15").time}},
                |    end: {unixMilliseconds: ${Timestamp.valueOf("2018-09-01 10:01:15").time}},
                |    patients: [{id: "${patient.id}"}]
                |}) {
                |   eid {
                |       id
                |   }
                |   title
                |   }
                |}""".trimMargin()
            )
            .exchange()
            .debugPrint()
            .verifyOnlyDataExists("addEvent")

        val addedEvent = hibernateEventRepository.findAll().find { it.title == "MY EVENT" }
        assertNotNull(addedEvent)

        val session = sessionFactory.openSession()
        try {
            patient.schedule.events.clear()
            hibernateEventRepository.delete(addedEvent!!)
            hibernatePatientRepository.delete(patient)
        } catch (e: Exception) {
            throw e
        } finally {
            session.close()
        }
    }
}