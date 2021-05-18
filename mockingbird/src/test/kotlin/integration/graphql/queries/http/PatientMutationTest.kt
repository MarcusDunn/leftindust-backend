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
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class PatientMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository
) {
    val count = hibernatePatientRepository.count()

    @BeforeEach
    internal fun setUp() {
        assert(hibernatePatientRepository.count() == count) { "leaked patients in PatientQueryTest" }
    }

    @AfterEach
    internal fun tearDown() {
        assert(hibernatePatientRepository.count() == count) { "leaked patients in PatientQueryTest" }
    }

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @Test
    internal fun `update patient`() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { uid } returns "admin"
        }, mockk(relaxed = true))

        val original = hibernatePatientRepository.save(EntityStore.patient("PatientMutationTest.update patient"))

        val query = "updatePatient"

        try {
            testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    """
                    |mutation { $query(patient: {
                    |   pid:${original.id!!},
                    |   nameInfo: {
                    |       firstName:"Ryan",
                    |       middleName:"Rodney",
                    |       lastName:"Reynolds"
                    |   },
                    |   addresses:[
                    |       {
                    |           address:"2706 Trafalgar St",
                    |           province:"BC",
                    |           city:"Vancouver",
                    |           postalCode:"V6K 2J6",
                    |           addressType:School,
                    |           country:Canada
                    |       }],
                    |   dateOfBirth: {
                    |       day:23,
                    |       month:Nov,
                    |       year:1976
                    |   },
                    |   emergencyContacts:[
                    |       {
                    |           firstName:"Blake",
                    |           lastName:"Lively",
                    |           phones:[
                    |               {
                    |                   number:"60418181919",
                    |                   type:Cell
                    |               }],
                    |           emails:[],
                    |           relationship:Partner
                    |       }],
                    |   emails:[
                    |       {
                    |           email:"ryan@aviationgin.com",
                    |           type:Other
                    |       }],
                    |   ethnicity:White,
                    |   sex:Male,
                    |   insuranceNumber:"9287 267 226",
                    |   phones:[
                    |       {
                    |           number:"60477383093",
                    |           type:Cell
                    |       }
                    |   ]
                    |}) {pid}}
                """.trimMargin()
                )
                .exchange()
                .verifyOnlyDataExists(query)
        } catch (e: Exception) {
            throw e
        } finally {
            hibernatePatientRepository.delete(original)
        }
    }
}