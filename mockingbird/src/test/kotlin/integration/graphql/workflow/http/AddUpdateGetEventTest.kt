package integration.graphql.workflow.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import integration.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.web.reactive.server.WebTestClient
import java.sql.Timestamp
import java.util.*

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class AddUpdateGetEventTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired private val hibernateDoctorRepository: HibernateDoctorRepository,

    ) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    val count = hibernateEventRepository.count()

    @BeforeEach
    @AfterEach
    internal fun checkLeaks() {
        assert(hibernateEventRepository.count() == count) { "leaked patients in AddUpdateGetEventTest" }
    }

    @Test
    internal fun addUpdateGetEventTest() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val uuid = addEvent()

        assertDoesNotThrow { hibernateEventRepository.getById(uuid) }

        val doctorUUID = updateEvent(uuid)

        getEvent(uuid)

        hibernateDoctorRepository.deleteById(doctorUUID)
        hibernateEventRepository.deleteById(uuid)
    }

    private fun getEvent(uuid: UUID) {
        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """query{ events(events: [{id: "$uuid"}]) {
                |       doctors {
                |        did {id}
                |        firstName
                |        lastName
                |       }
                |   }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("events")
            .jsonPath("data.events[0].doctors[0].firstName")
            .isEqualTo("lebron")
            .jsonPath("data.events[0].doctors[0].lastName")
            .isEqualTo("james")
    }

    private fun updateEvent(uuid: UUID): UUID {
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
                | }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addDoctor")
            .jsonPath("data.addDoctor.firstName")
            .isEqualTo("lebron")

        val doctor = hibernateDoctorRepository.findAll(Pageable.ofSize(10))
            .find { it.nameInfo.firstName == "lebron" && it.nameInfo.lastName == "james" }
        assertNotNull(doctor)
        doctor!!

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """
                |mutation { editEvent(event: {
                | eid: {id: "$uuid" }
                | doctors: [ {id: "${doctor.id!!}" }]
                |}) {
                |   eid {
                |       id
                |   }
                |   doctors {
                |        firstName
                |        lastName
                |   }
                |}
                |}""".trimMargin()
            )
            .exchange()
            .debugPrint()
            .verifyOnlyDataExists("editEvent")
            .jsonPath("data.editEvent.doctors[0].firstName")
            .isEqualTo("lebron")
            .jsonPath("data.editEvent.doctors[0].lastName")
            .isEqualTo("james")

        return doctor.id!!
    }

    private fun addEvent(): UUID {
        val start = Timestamp.valueOf("2018-09-01 09:01:15")
        val end = Timestamp.valueOf("2018-09-01 10:01:15")

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

        val events = hibernateEventRepository.getAllByTitleEquals("MY EVENT")
        assertEquals(1, events.size) { "events: " + events.map { it.id.toString() + " " + it.title + " " + it.description }.toString() }
        val first = events.first()
        assertEquals(false, first.allDay)
        return first.id!!
    }
}