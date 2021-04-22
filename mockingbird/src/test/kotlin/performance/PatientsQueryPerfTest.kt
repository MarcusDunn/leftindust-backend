package performance

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.queries.PatientQuery
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Performance")
@Transactional
class PatientsQueryPerfTest(
    @Autowired private val patientQuery: PatientQuery,
    @Autowired private val patientRepository: HibernatePatientRepository,
) {

    @Test
    internal fun `test getting patients by pid`() {
        val authContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk() {
                every { uid } returns "admin"
            }
        }

        val pids = (0 until 1000)
            .map {
            patientRepository.save(EntityStore.patient("PatientsQueryPerfTest.test getting patients by pid $it"))
        }
            .map { it.id!! }
            .map { gqlID(it) }

        assertPerf("PatientQuery.patients by pids", runs = 10, maxNanos = Duration.ofMillis(70).toNanos()) {
            runBlocking { patientQuery.patients(pids = pids, authContext = authContext) }
        }
    }
}