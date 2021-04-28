package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.ninjasquad.springmockk.MockkBean
import graphql.Assert
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
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
@Transactional
class PatientMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val patientRepository: HibernatePatientRepository,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun `create patient with address`() {
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val mutation = "addPatient"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """mutation { $mutation(patient: {
                |    nameInfo: {firstName: "patient", lastName: "heck"},
                |    dateOfBirth: {day: 23, month: Jan, year: 1999}, 
                |    addresses: [{addressType: Apartment, address: "1444 Poo poo st", city: "Vancouver", country: Canada, province: "Alberta", postalCode: "Poopoo"}],
                |    sex: Male
                |})
                | {
                | firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists(mutation)

        val result = patientRepository.findAll(PageRequest.of(0, 10))
            .iterator()
            .asSequence()
            .find { it.address.firstOrNull()?.address == "1444 Poo poo st" }
        Assert.assertNotNull(result)
    }
}