package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.util.EntityStore
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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
class DoctorMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val userRepository: HibernateUserRepository,
    @Autowired private val sessionFactory: SessionFactory,
) {
    private val doctorCount = doctorRepository.count()
    private val userCount = userRepository.count()

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @BeforeEach
    @AfterEach
    internal fun setUp() {
        assertEquals(
            doctorCount,
            doctorRepository.count()
        ) { "Leaked doctors in DoctorMutationTest ${doctorRepository.findAll().map { it.nameInfo }}" }
        assertEquals(userCount, userRepository.count()) {
            "Leaked users in DoctorMutationTest ${
                userRepository.findAll().map { it.nameInfo }
            }"
        }
    }

    @Test
    internal fun `create doctor with user`() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """mutation { addDoctor(doctor: {user: {uid: "new uid", nameInfo: {firstName: "doc", lastName: "james"}}, nameInfo: {firstName: "doc", lastName: "james"}, title: "khan", dateOfBirth: {day: 23, month: Jan, year: 1999}}) {
                | firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addDoctor")

        val result =
            doctorRepository.findAll().find { it.nameInfo.firstName == "doc" && it.nameInfo.lastName == "james" }!!
        val userResult = userRepository.findAll().find { it.uniqueId == "new uid" }!!

        doctorRepository.deleteById(result.id!!)
        userRepository.deleteById(userResult.id!!)
    }

    @Test
    internal fun `create doctor with address`() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """mutation { addDoctor(doctor: {
                |    nameInfo: {firstName: "lebron", lastName: "james"},
                |    dateOfBirth: {day: 23, month: Jan, year: 1999}, 
                |    addresses: [{addressType: Home, address: "182 yeet st", city: "Vancouver", country: Canada, province: "BritishColumbia", postalCode: "h7g1p1"}]
                |})
                | {
                | firstName
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addDoctor")
            .jsonPath("data.addDoctor.firstName")
            .isEqualTo("lebron")

        val result = doctorRepository.findAll(PageRequest.of(0, 10))
            .iterator()
            .asSequence()
            .find { it.addresses.firstOrNull()?.address == "182 yeet st" }!!

        doctorRepository.deleteById(result.id!!)
    }

    @Test
    internal fun `edit doctor`() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val doctor = doctorRepository.save(EntityStore.doctor("DoctorMutationTest.`edit doctor`"))

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """mutation { editDoctor(doctor: {did: {id: "${doctor.id!!}"}, nameInfo: {firstName: "biggus", lastName: "dickus"}, title: "Senator", emails: [{type: Work, email: "boss@leftindust.ca"}], phones: [{type: Cell, number: "+1 888 952 7421"}]})
                | {
                | did {
                |    id,
                |    __typename  
                |    }
                | __typename
                | firstName
                | }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("editDoctor")
            .jsonPath("data.editDoctor.did.id")
            .isEqualTo(doctor.id!!.toString())
            .jsonPath("data.editDoctor.firstName")
            .isEqualTo("biggus")

        doctorRepository.deleteById(doctor.id!!)
    }
}