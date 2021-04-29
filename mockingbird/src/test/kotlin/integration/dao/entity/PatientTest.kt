package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
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
) {

    @Test
    internal fun `set by gql input with doctors`() {
        val patient = patientRepository.save(EntityStore.patient("PatientTest.set by gql input with patients"))

        val attachedDoctor = doctorRepository.save(EntityStore.doctor("PatientTest.set by gql input with patients"))

        patient.addDoctor(attachedDoctor)

        val newDoctor = doctorRepository.save(EntityStore.doctor("PatientTest.set by gql input with patients 2"))

        val gqlInput = GraphQLPatientEditInput(
            pid = gqlID(patient.id!!),
            doctors = listOf(gqlID(newDoctor.id!!))
        )

        patient.setByGqlInput(gqlInput, sessionFactory.currentSession)

        assertEquals(newDoctor, patient.doctors.firstOrNull()?.doctor)
        assert(attachedDoctor.patients.isEmpty())
        assertFalse(patient.doctors.any { it.doctor == attachedDoctor })
    }
}