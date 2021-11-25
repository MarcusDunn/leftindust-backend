package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.AssignedForm
import com.leftindust.mockingbird.dao.impl.repository.HibernateAssignedFormRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormDataRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.ninjasquad.springmockk.MockkBean
import integration.APPLICATION_JSON_MEDIA_TYPE
import integration.GRAPHQL_ENDPOINT
import integration.GRAPHQL_MEDIA_TYPE
import integration.debugPrint
import integration.util.EntityStore
import integration.verifyOnlyDataExists
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import javax.persistence.EntityManager

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient(timeout = "PT15M")
@Tag("Integration")
class SurveyDataQueryIntegrationTest(
    @Autowired private val webTestClient: WebTestClient,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val formRepository: HibernateFormRepository,
    @Autowired private val formDataRepository: HibernateFormDataRepository,
    @Autowired private val assignedFormRepository: HibernateAssignedFormRepository,
) {

    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    fun `check can get patient assigned forms survey template`() {
        coEvery { contextFactory.generateContext(any()) } returns GraphQLAuthContext(mockk {
            every { isVerified() } returns true
        }, mockk(relaxed = true))

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val patient =
            patientRepository.save(EntityStore.patient("SurveyDataQueryIntegrationTest.check can get patient assigned forms survey template"))
        val form = formRepository.save(EntityStore.form("SurveyDataQueryIntegrationTest.check can get patient assigned forms survey template"))

        val assignedForm = assignedFormRepository.save(AssignedForm(form, patient))

        try {
            webTestClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(
                    //language=Graphql
                    """query {
                        patients(pids: {id: "${patient.id}"}) {
                           assignedForms {
                               id {
                                   id
                               }
                           }
                       }
                   }""".trimMargin()
                )
                .exchange()
                .debugPrint()
                .verifyOnlyDataExists("patients")
                .jsonPath("data.patients[0].assignedForms[0].id")
                .exists()
        } finally {
            assignedFormRepository.delete(assignedForm)
            formRepository.delete(form)
            patientRepository.delete(patient)
            assertEquals(0, assignedFormRepository.count()) {assignedFormRepository.findAll().toString()}
            assertEquals(0, formDataRepository.count()) { formDataRepository.findAll().toString()}
            assertEquals(0, patientRepository.count()) {patientRepository.findAll().map { it.nameInfo.firstName }.toString()}
        }
    }
}