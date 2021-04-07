package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.ninjasquad.springmockk.MockkBean
import graphql.Assert.assertNotNull
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
import org.springframework.data.domain.Example
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class DoctorMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val userRepository: HibernateUserRepository,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    @Transactional
    internal fun `create doctor with user`() {
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val mutation = "addDoctor"

        val uid = "new uid"

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                """mutation { $mutation(doctor: {user: {uid: "$uid"}, firstName: "doc", lastName: "james", title: "khan", dateOfBirth: {date: {day: 23, month: Jan, year: 1999}}}) {
                | firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists(mutation)

        val result = doctorRepository.findAll().find { it.firstName == "doc" && it.lastName == "james" }
        val userResult = userRepository.findAll().find { it.uniqueId == uid }
        assertNotNull(result)
        assertNotNull(userResult)
    }
}