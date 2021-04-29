package integration.dao.entity

import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import integration.util.EntityStore
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DoctorTest {

    @Test
    internal fun `set by gql input with patients`(
        @Autowired patientRepository: HibernatePatientRepository,
        @Autowired doctorRepository: HibernateDoctorRepository
    ) {
        val doctor = doctorRepository.save(EntityStore.doctor("DoctorTest.set by gql input with patients"))

        val attachedPatient = patientRepository.save(EntityStore.patient("DoctorTest.set by gql input with patients"))

        doctor.addPatient(attachedPatient)

        val newPatient = patientRepository.save(EntityStore.patient("DoctorTest.set by gql input with patients 2"))

        val gqlInput = GraphQLDoctorEditInput(
            did = gqlID(1000),
            patients = listOf(gqlID(newPatient.id!!))
        )

        doctor.setByGqlInput(gqlInput, mockk())

        Assertions.assertEquals(newPatient, doctor.patients.firstOrNull()?.patient)
        assert(attachedPatient.doctors.isEmpty())
        Assertions.assertFalse(doctor.patients.any { it.patient == attachedPatient })
    }
}