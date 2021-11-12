package integration.graphql.workflow.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.debugPrint
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
class AddGetDeleteForm(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val formRepository: HibernateFormRepository,
) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun createForm() {
        val mediqToken = mockk<MediqToken> {
            every { uid } returns "yeet"
        }
        val graphQLAuthContext = mockk<GraphQLAuthContext>(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        coEvery { contextFactory.generateContext(any()) } returns graphQLAuthContext

        testClient
            .post()
            .uri(GRAPHQL_ENDPOINT)
            .accept(APPLICATION_JSON_MEDIA_TYPE)
            .contentType(GRAPHQL_MEDIA_TYPE)
            .bodyValue(
                //language=graphql
                """
                    | mutation { addSurveyTemplate(survey: {
                    | name: "myForm",
                    | sections: [
                    |  {
                    |  name: "section 1",
                    |  number: 1,
                    |  fields: [
                    |   {
                    |   title: "how much is your weight?",
                    |   intUpperBound: 1000,
                    |   intLowerBound: 50,
                    |   number: 1,
                    |   dataType: Integer
                    |   }]},
                    |  {
                    |  name: "section 2",
                    |  number: 2,
                    |  fields: [
                    |   {
                    |   title: "your sex, give it to me"
                    |   dataType: SingleMuliSelect,
                    |   number: 1
                    |   multiSelectPossibilities: ["Male", "Female", "Other"]
                    |   }
                    |   ]
                    |  }
                    |  ]
                    |  }) {
                    |    name
                    |    sections {
                    |        name
                    |    }
                    | } 
                    |}
                    |""".trimMargin()
            ).exchange()
            .debugPrint()
            .expectBody()
            .jsonPath("data.addFormTemplate.name")
            .isEqualTo("myForm")
            .jsonPath("data.addFormTemplate.sections")
            .isArray

        formRepository.deleteAll()
        testClient.post()
    }
}