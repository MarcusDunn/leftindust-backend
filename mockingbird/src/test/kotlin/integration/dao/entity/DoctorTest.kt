package integration.dao.entity

import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
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
class DoctorTest(
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val sessionFactory: SessionFactory,
) {

    @Test
    internal fun `set by gql input with patients`() {
        val doctor = doctorRepository.save(EntityStore.doctor("DoctorTest.set by gql input with patients"))

        val attachedPatient = patientRepository.save(EntityStore.patient("DoctorTest.set by gql input with patients"))

        doctor.addPatient(attachedPatient)

        val newPatient = patientRepository.save(EntityStore.patient("DoctorTest.set by gql input with patients 2"))

        val gqlInput = GraphQLDoctorEditInput(
            did = gqlID(doctor.id!!),
            patients = listOf(gqlID(newPatient.id!!))
        )

        doctor.setByGqlInput(gqlInput, sessionFactory.currentSession)

        assertEquals(newPatient, doctor.patients.firstOrNull()?.patient)
        assert(attachedPatient.doctors.isEmpty())
        assertFalse(doctor.patients.any { it.patient == attachedPatient })
    }
}