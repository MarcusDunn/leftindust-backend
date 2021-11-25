package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import integration.debugPrint
import integration.util.EntityStore
import integration.util.NoAuthTest
import integration.util.gqlRequest
import integration.verifyOnlyDataExists
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@AutoConfigureWebTestClient
@Tag("Integration")
class SurveyTemplateMutationIntegrationTest(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val formRepository: HibernateFormRepository,
) : NoAuthTest() {

    @Test
    fun `test assigning survey to patient`() {
        val patient = patientRepository.save(EntityStore.patient("SurveyTemplateMutationIntegrationTest.test assigning survey to patient"))
        val form = formRepository.save(EntityStore.form("SurveyTemplateMutationIntegrationTest.test assigning survey to patient"))

        webTestClient.gqlRequest(
            //language=GraphQL
            """
            mutation {
              assignSurvey(patients: [{id: "${patient.id}"}], survey: {id: "${form.id}"}) {
                  pid {
                      id
                  }
              }
            }
        """.trimIndent()
        )
            .debugPrint()
            .verifyOnlyDataExists("assignSurvey")
            .jsonPath("data.assignSurvey[0].pid.id")
            .isEqualTo(patient.id!!.toString())
    }
}