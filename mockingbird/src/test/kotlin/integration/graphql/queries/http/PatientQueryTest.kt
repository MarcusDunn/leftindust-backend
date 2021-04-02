package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.ninjasquad.springmockk.MockkBean
import integration.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
@Transactional
class PatientQueryTest(
    @Autowired private val testClient: WebTestClient,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Autowired
    private lateinit var patientRepository: HibernatePatientRepository

    @Test
    internal fun `test search patient`() {

        coEvery { contextFactory.generateContext(any()) } returns mockk() {
            every { getHTTPRequestHeader(any()) } returns null
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        assertDoesNotThrow {
            val mutation = "addPatient"

            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    """mutation { $mutation(patient: {firstName: "marcus", lastName: "dunn", sex: Male, dateOfBirth: {date: {day: 23, month: Mar, year: 2001}}}) { 
                |   firstName
                |   }
                |} """.trimMargin()
                )
                .exchange()
                .expectBody()
                .jsonPath("$.data.addPatient.firstName")
                .isEqualTo("marcus")
        }

        assertDoesNotThrow {
            val query = "patients"

            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    """query { $query(example: {personalInformation: {firstName: {eq: "marcus"}}}) { 
                |   firstName
                |   }
                |} """.trimMargin()
                )
                .exchange()
                .expectBody()
                .jsonPath("$.data.patients[0].firstName")
                .isEqualTo("marcus")
        }
    }
}