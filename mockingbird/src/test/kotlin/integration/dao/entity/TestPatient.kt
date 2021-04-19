package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
class TestPatient(@Autowired private val patientDao: PatientDao) {

    @Test
    internal fun `test patient does not persist on invalid arguments`(@Autowired patientRepository: HibernatePatientRepository) {
        val requester = mockk<MediqToken>() {
            every { uid } returns "admin"
        }

        val testName = "TestPatient.test patient does not persist on invalid arguments"
        val graphQLPatientInput = EntityStore.graphQLPatientInput(testName)

        runBlocking {
            assertThrows<IllegalArgumentException> {
                patientDao.addNewPatient(graphQLPatientInput, requester)
            }
        }

        assertEquals(null, patientRepository.findAll(PageRequest.of(0, 100)).find { it.middleName == testName })
    }
}