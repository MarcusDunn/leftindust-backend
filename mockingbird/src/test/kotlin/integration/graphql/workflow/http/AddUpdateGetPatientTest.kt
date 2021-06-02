package integration.graphql.workflow.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.example.StringFilter
import com.ninjasquad.springmockk.MockkBean
import integration.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class AddUpdateGetPatientTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val patientDao: PatientDao,
    @Autowired private val eventDao: EventDao,
    ) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    val count = hibernatePatientRepository.count()

    @BeforeEach
    @AfterEach
    internal fun checkLeaks() {
        assert(hibernatePatientRepository.count() == count) { "leaked patients in AddUpdateGetPatientTest" }
    }

    @Test
    internal fun `add update get patient`() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { uid } returns "admin"
        }, mockk(relaxed = true))
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val uuid = addPatient()
        addEvent(uuid)
        updatePatient(uuid)
        getPatient(uuid)
        deletePatient(uuid)
        assert(hibernatePatientRepository.findById(uuid).isEmpty)
    }

    private fun addEvent(uuid: UUID) {
        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """ mutation { addEvent(event: {patients: [{id: "$uuid"}] start: {unixMilliseconds: 100000000}, end: {unixMilliseconds: 110000000}, allDay: false, title: "some event", description: "a big ol event"}) {
                | patients {
                |  firstName
                |  lastName
                |  }
                | }  
                |}
            """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addEvent")
            .jsonPath("data.addEvent.patients[0].firstName")
            .isEqualTo("Clyde")
            .jsonPath("data.addEvent.patients[0].lastName")
            .isEqualTo("Bronstone")

        val events = runBlocking { eventDao.getByPatient(GraphQLPatient.ID(uuid), mockk()) }
        assertEquals("a big ol event", events.first().description)
        val eventPatientId = runBlocking { patientDao.getByEvent(GraphQLEvent.ID(events.first().id!!), mockk()) }.first().id
        assertEquals(uuid, eventPatientId)
    }

    private fun addPatient(): UUID {
        val response = testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """ mutation { addPatient(patient: {sex: Male, dateOfBirth: {year: 2000, month: Apr, day: 1}, nameInfo: {firstName: "Clyde", lastName: "Bronstone"}}) 
                | {
                | firstName
                | lastName
                | pid {id}
                | }
                |}
            """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addPatient")
            .jsonPath("data.addPatient.firstName")
            .isEqualTo("Clyde")
            .jsonPath("data.addPatient.lastName")
            .isEqualTo("Bronstone")

        val id = runBlocking {
            patientDao.searchByExample(
                GraphQLPatientExample(
                    firstName = StringFilter(
                        eq = "Clyde",
                        strict = true
                    ), lastName = StringFilter(
                        eq = "Bronstone",
                        strict = true
                    ),
                    strict = true
                ), mockk()
            )
        }.first().id!!

        response
            .jsonPath("data.addPatient.pid.id")
            .isEqualTo(id.toString())

        return id
    }

    private fun updatePatient(id: UUID) {
        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """ mutation { updatePatient(patient: {pid: {id: "$id"} nameInfo: {middleName: "Galiano"}}) 
                | {
                | firstName
                | middleName
                | pid {id}
                | }
                |}
            """.trimMargin()
            )
            .exchange()
            .debugPrint()
            .verifyOnlyDataExists("updatePatient")
            .jsonPath("data.updatePatient.middleName")
            .isEqualTo("Galiano")
            .jsonPath("data.updatePatient.firstName")
            .isEqualTo("Clyde")
            .jsonPath("data.updatePatient.pid.id")
            .isEqualTo(id.toString())
    }

    private fun getPatient(id: UUID) {
        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """ query { patients(pids: [{id: "$id"}]) 
                | {
                | firstName
                | middleName
                | pid {id}
                | }
                |}
            """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("patients")
            .jsonPath("data.patients[0].middleName")
            .isEqualTo("Galiano")
            .jsonPath("data.patients[0].firstName")
            .isEqualTo("Clyde")
            .jsonPath("data.patients[0].pid.id")
            .isEqualTo(id.toString())
    }

    fun deletePatient(id: UUID) {
        hibernatePatientRepository.deleteById(id)
    }
}