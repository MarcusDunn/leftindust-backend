package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import graphql.Assert
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.util.EntityStore
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class PatientMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val sessionFactory: SessionFactory,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `create patient with address`() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val doctor = doctorRepository.save(EntityStore.doctor("PatientMutationTest.create patient with address"))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=Graphql
                """mutation { addPatient(patient: {
                |    nameInfo: {firstName: "patient", lastName: "joe"},
                |    dateOfBirth: {day: 23, month: Jan, year: 1999}, 
                |    addresses: [{
                |        addressType: Apartment,
                |        address: "1444 main st",
                |        city: "Vancouver", 
                |        country: Canada, 
                |        province: "Alberta", 
                |        postalCode: "weuhfw"
                |        }],
                |    sex: Male
                |    doctors: [{id: "${doctor.id}"}]
                |})
                |    {
                |    pid {
                |        id
                |    }
                |    firstName
                |    }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addPatient")

        val result = patientRepository.findAll(PageRequest.of(0, 10))
            .iterator()
            .asSequence()
            .find { it.address.firstOrNull()?.address == "1444 main st" }!!

        val session = sessionFactory.openSession()
        try {
            doctor.patients.clear()
            result.doctors.clear()
            doctorRepository.delete(doctor)
            patientRepository.delete(result)
        } catch (e: Exception) {
            throw e
        } finally {
            session.close()
        }
    }
}