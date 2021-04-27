package integration.graphql.queries

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.queries.PatientQuery
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.example.StringFiler
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@Tag("Integration")
class PatientQueryTest {

    @Autowired
    private lateinit var hibernatePatientRepository: HibernatePatientRepository

    @Autowired
    private lateinit var patientQuery: PatientQuery

    private val mockkAuthContext = mockk<GraphQLAuthContext> {
        every { mediqAuthToken } returns mockk {
            every { uid } returns "admin"
        }
    }

    @Test
    internal fun `get patient by single pid`() {
        runBlocking {
            // Setup
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.get patient by single pid")
                hibernatePatientRepository.save(patientExample)
            }

            // action
            val patients =
                patientQuery.patients(pids = listOf(gqlID(patientEntity.id!!)), authContext = mockkAuthContext)

            // assert
            val expected = GraphQLPatient(patientEntity, patientEntity.id!!, authContext = mockkAuthContext)
            assertEquals(listOf(expected), patients)
        }
    }

    @Test
    internal fun getPatientPhones() {
        runBlocking {
            // Setup
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.getPatientPhones")
                hibernatePatientRepository.save(patientExample)
            }

            // action
            val patients =
                patientQuery.patients(pids = listOf(gqlID(patientEntity.id!!)), authContext = mockkAuthContext)

            // assert
            val expected = GraphQLPatient(patientEntity, patientEntity.id!!, authContext = mockkAuthContext)
            assertEquals(listOf(expected), patients)
        }
    }

    @Test
    internal fun searchByExample() {
        runBlocking {
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.searchByExample")
                hibernatePatientRepository.save(patientExample)
            }

            val result = patientQuery.patients(
                example = GraphQLPatientExample(
                    firstName = StringFiler(
                        eq = patientEntity.nameInfo.firstName
                    )
                ),
                authContext = mockkAuthContext
            )

            val expected = listOf(GraphQLPatient(patientEntity, patientEntity.id!!, mockkAuthContext))

            assertEquals(expected, result)
        }
    }
}