package integration.dao

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
class EventDaoTest(
    @Autowired private val eventDao: EventDao,
    @Autowired private val patientRepository: HibernatePatientRepository
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `get event by patient`() {
        coEvery {authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val patient = patientRepository.save(EntityStore.patient("EventDaoTest.`get event by patient").apply { addEvent(EntityStore.event("EventDaoTest.`get event by patient`")) })

        val result = runBlocking { eventDao.getByPatient(GraphQLPatient.ID(patient.id!!), mockk()) }

        patient.events.clear()
        println(patientRepository.findAll())
        patientRepository.delete(patient)

        assertEquals(1, result.size)
    }
}