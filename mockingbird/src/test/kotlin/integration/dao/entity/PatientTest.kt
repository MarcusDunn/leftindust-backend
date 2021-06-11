package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateEventRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientEditInput
import integration.util.EntityStore
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
class PatientTest(
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val sessionFactory: SessionFactory,
    @Autowired private val eventRepository: HibernateEventRepository,
) {

    @Test
    internal fun `set by gql input with doctors`() {
        val patient = patientRepository.save(EntityStore.patient("PatientTest.set by gql input with patients"))

        val attachedDoctor = doctorRepository.save(EntityStore.doctor("PatientTest.set by gql input with patients"))

        patient.addDoctor(attachedDoctor)

        val newDoctor = doctorRepository.save(EntityStore.doctor("PatientTest.set by gql input with patients 2"))

        val gqlInput = GraphQLPatientEditInput(
            pid = GraphQLPatient.ID(patient.id!!),
            doctors = listOf(GraphQLDoctor.ID(newDoctor.id!!))
        )

        patient.setByGqlInput(gqlInput, sessionFactory.currentSession)

        assertEquals(newDoctor, patient.doctors.firstOrNull()?.doctor)
        assertEquals(0, attachedDoctor.patients.size)
        assertFalse(patient.doctors.any { it.doctor == attachedDoctor })
    }

    @Test
    fun addEvent() {
        val patient = patientRepository.save(EntityStore.patient("ScheduleTest.addEvent"))
        val event = eventRepository.save(EntityStore.event("ScheduleTest.addEvent"))
        patient.addEvent(event)

        val events = patientRepository.getById(patient.id!!).events

        assertEquals(1, events.size)
    }
}