package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.dao.entity.Patient
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.hibernate.SessionFactory
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
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun `test search patient`(@Autowired sessionFactory: SessionFactory) {
        val patient = EntityStore.patient()

        // setup
        val id = run {
            val session = sessionFactory.openSession()
            val id = session.save(patient) as Long
            session.close()
            id
        }

        coEvery { contextFactory.generateContext(any()) } returns mockk() {
            every { getHTTPRequestHeader(any()) } returns null
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val query = "patients"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """query { $query(example: {personalInformation: {firstName: {eq: "${patient.firstName}"}}}) { 
                |   firstName
                |   }
                |} """.trimMargin()
            )
            .exchange()
            .expectBody()
            .jsonPath("$.data.patients[0].firstName")
            .isEqualTo(patient.firstName)

        // cleanup
        run {
            val session = sessionFactory.openSession()
            session.delete(session.get(Patient::class.java, id))
            session.close()
        }
    }
}