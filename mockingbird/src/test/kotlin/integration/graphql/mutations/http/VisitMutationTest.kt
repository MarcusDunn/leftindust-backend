package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.input.GraphQLVisitInput
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.util.EntityStore
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*
import kotlin.random.Random

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class VisitMutationTest(
    @Autowired private val testClient: WebTestClient,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @MockkBean
    private lateinit var visitDao: VisitDao

    @Test
    internal fun createVisit() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed
        val slot = slot<GraphQLVisitInput>()
        val event = mockk<Event>(relaxed = true) {
            every { id } returns UUID.randomUUID()
        }

        coEvery { visitDao.addVisit(capture(slot), any()) } answers { Visit(slot.captured, event).apply { id = UUID.randomUUID() } }

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
                    |   foundationIcdCodes: [{url: "122222"}],
                    |   })  {
                    |   vid {
                    |    id
                    |    }
                    |   }
                    |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addVisit")
    }
}