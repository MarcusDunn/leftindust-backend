package integration.dao

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
class EventDaoTest(
    @Autowired private val eventDao: EventDao,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val eventRepository: HibernateEventRepository
) {
    private val eventCount = eventRepository.count()
    private val patientCount = patientRepository.count()

    @BeforeEach
    @AfterEach
    fun checkLeaks() {
        assert(patientCount == patientRepository.count()) {"leaked patients in EventDaoTest"}
        assert(eventCount == eventRepository.count()) {"leaked events in EventDaoTest"}
    }

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    @Transactional
    internal fun `get event by patient`() {
        coEvery {authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val eventEntity = EntityStore.event("EventDaoTest.`get event by patient`")
        eventRepository.save(eventEntity)
        val patient = patientRepository.save(EntityStore.patient("EventDaoTest.`get event by patient"))
        patient.addEvent(eventEntity)

        val result = runBlocking { eventDao.getByPatient(GraphQLPatient.ID(patient.id!!), mockk()) }


        assertEquals(1, result.size)

        patient.events.clear()
        eventRepository.delete(eventEntity)
        patientRepository.delete(patient)
    }
}