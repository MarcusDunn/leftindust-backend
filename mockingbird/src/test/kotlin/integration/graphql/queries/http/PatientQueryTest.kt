package integration.graphql.queries.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
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
import org.springframework.data.domain.Pageable
import org.springframework.test.web.reactive.server.WebTestClient
import java.sql.Timestamp
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Transactional
@Tag("Integration")
class PatientQueryTest {
    @Autowired
    private lateinit var testClient: WebTestClient

    @Autowired
    private lateinit var patientRepository: HibernatePatientRepository

    @MockkBean
    private lateinit var authorizer: Authorizer

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun `search patient`() {
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
                //language=Graphql
                """mutation { addPatient(patient: {
                |    nameInfo: {firstName: "Lillian", lastName: "joe"},
                |    dateOfBirth: {day: 23, month: Jan, year: 1999}, 
                |    sex: Male,
                |    insuranceNumber: "he ll o wo r ld "
                |})
                |  {
                |    pid {
                |        id
                |    }
                |  }
                |}
                |""".trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addPatient")

        val patient = patientRepository.findAll(Pageable.ofSize(10))
            .find { it.nameInfo.firstName == "Lillian" && it.nameInfo.lastName == "joe" }!!

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
                |    description: "YO YO YO this do be an event doe + PatientQueryTest.search patient",
                |    allDay: false,
                |    start: {unixMilliseconds: ${start.time}},
                |    end: {unixMilliseconds: ${end.time}},
                |    patients: [{id: "${patient.id!!}"}]
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

        val event = patientRepository.findAll(Pageable.ofSize(10))
            .find { it.nameInfo.firstName == "Lillian" && it.nameInfo.lastName == "joe" }!!.events.first()

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """mutation { addVisit(visit: {
                    |   eid: {id: "${event.id}"},
                    |   title: "my visit from testing",
                    |   description: "patient has lost thier left foot",
                    |   foundationIcdCodes: [{url: "122222"}, {url: "188882"}],
                    |   })  {
                    |   vid {
                    |    id
                    |    }
                    |   }
                    |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addVisit")

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """query { patients(example: {icdCodes: {strict: true, includes: [{url: "122222"}]}, strict: true})  {
                    |   pid {
                    |    id
                    |    }
                    |   }
                    |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("patients")
            .jsonPath("data.patients[0].pid.id")
            .isEqualTo(patient.id!!.toString())

        testClient.post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=GraphQL
                """query { patients(example: {
                    |         strict:false
                    |         firstName:{
                    |            startsWith:"lill"
                    |            endsWith:"lill"
                    |            contains:"lill"
                    |            eq:"lill"
                    |            strict:false
                    |         }
                    |         lastName:{
                    |            startsWith:"lill"
                    |            endsWith:"lill"
                    |            contains:"lill"
                    |            eq:"lill"
                    |            strict:false
                    |         },
                    |         insuranceNumber:{
                    |            eq:"lill"
                    |            strict:true
                    |         }
                    |      }
                    |)  {
                    |   pid {
                    |    id
                    |    }
                    |   }
                    |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("patients")
            .jsonPath("data.patients[0].pid.id")
            .isEqualTo(patient.id!!.toString())
    }
}