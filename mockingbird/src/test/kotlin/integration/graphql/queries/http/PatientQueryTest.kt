package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.util.EntityStore
import integration.verifyData
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
class PatientQueryTest(
    @Autowired private val testClient: WebTestClient,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun getManyPatientsSortedByFirstName(@Autowired hibernatePatientRepository: HibernatePatientRepository) {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { uid } returns "admin"
        }, mockk(relaxed = true))

        val patients = (0 until 100).reversed().map {
            hibernatePatientRepository.save(
                EntityStore.patient("PatientQueryTest.getManyPatientsSortedByFirstName").apply {
                    nameInfo.firstName = "${nameInfo.firstName} + $it"
                })
        }

        val query = "patients"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """query{
                |   $query(range: {from: 0, to: 1} sortedBy: FIRST_NAME) {
                |       pid
                |       firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyData(
                query,
                """{pid=100, firstName=marcus + 0}"""
            )

        patients.forEach { hibernatePatientRepository.delete(it) }
    }
}