package integration.graphql.queries

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.queries.PatientQuery
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPersonExample
import com.leftindust.mockingbird.graphql.types.examples.StringFilter
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

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
                val patientExample = Patient(firstName = "marcus", lastName = "dunn", sex = Sex.Male)
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
    internal fun `search patient by name`() {
        runBlocking {
            // Setup
            val patientEntity = run {
                val patientExample = Patient(firstName = "marcus", lastName = "dunn", sex = Sex.Male)
                hibernatePatientRepository.save(patientExample)
            }

            // action
            val patients =
                patientQuery.patients(
                    example = GraphQLPatientExample(
                        personalInformation = GraphQLPersonExample(
                            firstName = StringFilter(includes = "marcus"),
                        )
                    ), authContext = mockkAuthContext
                )

            // assert
            val expected = GraphQLPatient(patientEntity, patientEntity.id!!, authContext = mockkAuthContext)
            assertEquals(listOf(expected), patients)
        }
    }
}