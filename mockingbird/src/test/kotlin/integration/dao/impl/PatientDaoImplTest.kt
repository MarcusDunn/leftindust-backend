package integration.dao.impl

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.examples.GraphQLPersonExample
import com.leftindust.mockingbird.graphql.types.examples.GraphQLTimeExample
import com.leftindust.mockingbird.graphql.types.examples.StringFilter
import integration.util.EntityStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp
import javax.transaction.Transactional


@SpringBootTest(classes = [MockingbirdApplication::class])
@Transactional
@Tag("Integration")
class PatientDaoImplTest {
    @Autowired
    private lateinit var patientDao: PatientDao

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    internal fun `search by example`() {
        // setup
        val pid = session.save(EntityStore.patient("PatientDaoImplTest.search by example")) as Long
        val expected = session.get(Patient::class.java, pid)
        val mockkMediqToken = mockk<MediqToken> {
            every { uid } returns "admin"
        }

        // action
        val result = runBlocking {
            patientDao.searchByExample(
                GraphQLPatientExample(
                    personalInformation = GraphQLPersonExample(
                        firstName = StringFilter(eq = "marcus")
                    )
                ),
                mockkMediqToken
            )
        }.getOrThrow()

        // assert
        assertTrue(result.contains(expected))
    }

    @Test
    internal fun `search by example time`() {
        // setup
        val pid = session.save(EntityStore.patient("PatientDaoImplTest.search by example time")) as Long
        val expected = session.get(Patient::class.java, pid)
        val mockkMediqToken = mockk<MediqToken> {
            every { uid } returns "admin"
        }

        // action
        val result = runBlocking {
            patientDao.searchByExample(
                GraphQLPatientExample(
                    dateOfBirth = GraphQLTimeExample(
                        beforeUnixMilli = Timestamp.valueOf("2021-01-02 09:01:15").time,
                        afterUnixMilli = Timestamp.valueOf("2019-01-02 09:01:15").time,
                    )
                ),
                mockkMediqToken
            )
        }.getOrThrow()

        // assert
        assertTrue(result.contains(expected))
    }
}