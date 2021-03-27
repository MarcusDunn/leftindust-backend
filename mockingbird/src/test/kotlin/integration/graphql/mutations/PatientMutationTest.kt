package integration.graphql.mutations

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.mutations.PatientMutation
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@Tag("Integration")
class PatientMutationTest(
    @Autowired private val patientMutation: PatientMutation
) {
    @Autowired
    lateinit var sessionFactory: SessionFactory

    val session: Session
        get() = sessionFactory.currentSession

    @Test
    internal fun addPatient() {
        val authContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }
        runBlocking {
            val patientInput = EntityStore.graphQLPatientInput(authContext)

            val result = patientMutation.addPatient(patientInput, authContext)

            val result2 = session.get(Patient::class.java, result.pid.toLong())

            println(result2)
        }
    }
}