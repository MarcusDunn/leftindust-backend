package integration.graphql.mutations

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.mutations.PatientMutation
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/*
  This class is intentionally *not* marked as @Transactional in order to target a bug that only existed when there was
  no session open. As a result all tests MUST clean up after themselves.
*/

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
class PatientMutationTest(
    @Autowired private val patientMutation: PatientMutation,
    @Autowired private val patientRepository: HibernatePatientRepository
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun addPatient() {
        val mediqToken = mockk<MediqToken>()
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mediqToken
        }
        coEvery { authorizer.getAuthorization(any(), mediqToken) } returns Authorization.Allowed

        val patientInput = EntityStore.graphQLPatientInput("PatientMutationTest.addPatient")
        val result = runBlocking { patientMutation.addPatient(patientInput, authContext) }
        assertEquals(true, patientRepository.findById(result.pid.id).isPresent)
        patientRepository.delete(patientRepository.getById(result.pid.id))
        assertEquals(false, patientRepository.findById(result.pid.id).isPresent)
    }
}