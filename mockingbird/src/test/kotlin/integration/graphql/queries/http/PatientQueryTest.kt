package integration.graphql.queries.http

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
class PatientQueryTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
) {
    val patientCount = hibernatePatientRepository.count()
    val eventCount = hibernateEventRepository.count()

    @BeforeEach
    @AfterEach
    internal fun leakCheck() {
        assert(hibernatePatientRepository.count() == patientCount) { "leaked patients in PatientQueryTest" }
        assert(hibernateEventRepository.count() == eventCount) { "leaked events in PatientQueryTest" }
    }

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    lateinit var authorizer: Authorizer

    @Test
    internal fun `get events via patient`() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patient = hibernatePatientRepository.save(
            EntityStore.patient("PatientQueryTest.get events via patient")
                .apply { addEvent(EntityStore.event("PatientQueryTest.get events via patient")) }
        )

        try {
            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    //language=Graphql
                    """query {patients(pids: [{id: "${patient.id}"}]) {
                |events {
                |    title
                |    startTime {
                |        unixMilliseconds
                |    }
                |    endTime {
                |        unixMilliseconds
                |    }
                |    allDay
                |}
                |}
                |}
                """.trimMargin()
                )
                .exchange()
                .debugPrint()
                .expectBody()
                .jsonPath("$DATA_JSON_PATH.patients[0].events[0].title")
                .isEqualTo(patient.events.first().title)

        } catch (e: Exception) {
            throw e
        } finally {
            patient.events.clear()
            hibernatePatientRepository.deleteById(patient.id!!)
        }

    }

    @Test
    internal fun getManyPatientsSortedByFirstName() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patients = (0 until 100).reversed().map {
            hibernatePatientRepository.save(
                EntityStore.patient("PatientQueryTest.getManyPatientsSortedByFirstName").apply {
                    nameInfo.firstName = "${nameInfo.firstName} + $it"
                })
        }

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=Graphql
                """query{
                |   patients(range: {from: 0, to: 2} sortedBy: FIRST_NAME) {
                |       pid {
                |            id
                |       }
                |       firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .expectBody()
            .jsonPath("$DATA_JSON_PATH.patients[0].firstName")
            .isEqualTo("marcus + 0")

        patients.forEach { hibernatePatientRepository.delete(it) }
    }

    @Test
    internal fun search() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patient = hibernatePatientRepository.save(EntityStore.patient("PatientQueryTest.get events via patient"))

        try {
            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    //language=Graphql
                    """query {patients(example: {firstName: {contains: "arcu", strict: true} strict: true}) {
                |        firstName
                |        lastName
                |        middleName
                |        }
                |    }
                """.trimMargin()
                )
                .exchange()
                .debugPrint()
                .expectBody()
                .jsonPath("$DATA_JSON_PATH.patients[0].firstName")
                .isEqualTo(patient.nameInfo.firstName)
        } catch (e: Exception) {
            throw e
        } finally {
            hibernatePatientRepository.deleteById(patient.id!!)
        }
    }
}