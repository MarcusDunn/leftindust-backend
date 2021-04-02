package integration.graphql.mutations

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.mutations.PatientMutation
import integration.util.EntityStore
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
    @Test
    internal fun addPatient() {
        val authContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }
        val patientInput = EntityStore.graphQLPatientInput("PatientMutationTest.addPatient")
        val result = runBlocking { patientMutation.addPatient(patientInput, authContext) }
        assertEquals(true, patientRepository.findById(result.pid.toLong()).isPresent)
        patientRepository.delete(patientRepository.getOne(result.pid.toLong()))
        assertEquals(false, patientRepository.findById(result.pid.toLong()).isPresent)
    }
}