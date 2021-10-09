package integration.dao

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
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
import javax.transaction.Transactional

    @SpringBootTest(classes = [MockingbirdApplication::class])
    @Tag("Integration")
@Transactional
class ClinicDaoTest(
    @Autowired private val clinicDao: ClinicDao,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val clinicRepository: HibernateClinicRepository,
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `test get clinic by doctor`() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val doctor = doctorRepository.save(
            EntityStore.doctor("ClinicDaoTest.test get clinic by doctor"))

        val clinic = clinicRepository.save(
            Clinic(
                name = "name",
                address = EntityStore.address("ClinicDaoTest.test get clinic by doctor"),
                doctors = setOf(doctor)
            )
        )

        val requester = mockk<MediqToken>() {
            every { uid } returns "admin"
        }

        val result = runBlocking { clinicDao.getByDoctor(GraphQLDoctor.ID(doctor.id!!), requester) }

        assertEquals(listOf(clinic), result)
    }
}