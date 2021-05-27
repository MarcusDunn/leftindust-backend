package integration.graphql.mutations.http

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.ContextFactory
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Event
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
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
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(classes = [MockingbirdApplication::class])
@AutoConfigureWebTestClient
@Tag("Integration")
class VisitMutationTest(
    @Autowired private val testClient: WebTestClient,
    @Autowired private val hibernateEventRepository: HibernateEventRepository,
    @Autowired private val hibernateVisitRepository: HibernateVisitRepository,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
    @Autowired private val hibernateDoctorRepository: HibernateDoctorRepository,
) {
    @MockkBean
    private lateinit var contextFactory: ContextFactory

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun createVisit() {
        val mediqToken = mockk<MediqToken>()
        coEvery { contextFactory.generateContext(any()) } returns mockk(relaxed = true) {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val event = addEvent()
        val doctor = addDoctor()
        val patient = addPatient()
        doctor.addPatient(patient)


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
                    |   foundationIcdCode: {code: "122222"},
                    |   })  {
                    |   vid {
                    |    id
                    |    }
                    |   }
                    |} """.trimMargin()
            )
            .exchange()
            .verifyOnlyDataExists("addVisit")

        removeVisitsByEvent(event)
        removeEvent(event)
        removeDoctor(doctor)
        removePatient(patient)

    }

    private fun removeDoctor(doctor: Doctor) {
        hibernateDoctorRepository.delete(doctor)
    }

    private fun removePatient(patient: Patient) {
        hibernatePatientRepository.delete(patient)
    }

    private fun removeEvent(event: Event) {
        hibernateEventRepository.delete(event)
    }

    private fun removeVisitsByEvent(event: Event) {
        hibernateVisitRepository.delete(hibernateVisitRepository.getByEvent_Id(event.id!!))
    }

    private fun addPatient(): Patient {
        return hibernatePatientRepository.save(EntityStore.patient("VisitMutationTest.createVisit"))!!
    }

    private fun addDoctor(): Doctor {
        return hibernateDoctorRepository.save(EntityStore.doctor("VisitMutationTest.createVisit"))!!
    }

    private fun addEvent(): Event {
        return hibernateEventRepository.save(EntityStore.event("VisitMutationTest.createVisit"))
    }
}