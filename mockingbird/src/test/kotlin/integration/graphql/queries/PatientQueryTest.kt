package integration.graphql.queries

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.queries.PatientQuery
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.example.StringFilter
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
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@Tag("Integration")
class PatientQueryTest {

    @Autowired
    private lateinit var hibernatePatientRepository: HibernatePatientRepository

    @Autowired
    private lateinit var patientQuery: PatientQuery

    @MockkBean
    private lateinit var authorizer: Authorizer

    private val mockkAuthContext = mockk<GraphQLAuthContext> {
        every { mediqAuthToken } returns mockk {
            every { uid } returns "admin"
        }
    }

    @Test
    internal fun `get patient by single pid`() {
        coEvery { authorizer.getAuthorization(any(), mockkAuthContext.mediqAuthToken) } returns Authorization.Allowed
        runBlocking {
            // Setup
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.get patient by single pid")
                hibernatePatientRepository.save(patientExample)
            }

            // action
            val patients =
                patientQuery.patients(
                    pids = listOf(GraphQLPatient.ID(patientEntity.id!!)),
                    authContext = mockkAuthContext
                )

            // assert
            val expected = GraphQLPatient(patientEntity, authContext = mockkAuthContext)
            assertEquals(listOf(expected), patients)
        }
    }

    @Test
    internal fun getPatientPhones() {
        coEvery { authorizer.getAuthorization(any(), mockkAuthContext.mediqAuthToken) } returns Authorization.Allowed

        runBlocking {
            // Setup
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.getPatientPhones")
                hibernatePatientRepository.save(patientExample)
            }

            // action
            val patients =
                patientQuery.patients(
                    pids = listOf(GraphQLPatient.ID(patientEntity.id!!)),
                    authContext = mockkAuthContext
                )

            // assert
            val expected = GraphQLPatient(patientEntity, authContext = mockkAuthContext)
            assertEquals(listOf(expected), patients)
        }
    }

    @Test
    internal fun searchByExample() {
        coEvery { authorizer.getAuthorization(any(), mockkAuthContext.mediqAuthToken) } returns Authorization.Allowed

        runBlocking {
            val patientEntity = run {
                val patientExample = EntityStore.patient("PatientQueryTest.searchByExample")
                hibernatePatientRepository.save(patientExample)
            }

            val result = patientQuery.patients(
                example = GraphQLPatientExample(
                    firstName = StringFilter(
                        eq = patientEntity.nameInfo.firstName,
                        strict = true
                    ),
                    strict = true,
                ),
                authContext = mockkAuthContext
            )

            val expected = listOf(GraphQLPatient(patientEntity, mockkAuthContext))

            assertEquals(expected, result)
        }
    }
}