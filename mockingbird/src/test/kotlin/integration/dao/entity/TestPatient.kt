package integration.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.MockingbirdApplication
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(classes = [MockingbirdApplication::class])
@Tag("Integration")
@Transactional
class TestPatient {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    val session: Session
        get() = sessionFactory.currentSession

    @Test
    internal fun `test patient does not persist on invalid arguments`(@Autowired patientRepository: HibernatePatientRepository) {
        val graphQLPatientInput = GraphQLPatientInput(
            firstName = OptionalInput.Defined("mar"),
            lastName = OptionalInput.Defined("mar"),
            sex = OptionalInput.Defined(Sex.Male),
            pid = OptionalInput.Defined(ID("12"))
        )
        assertThrows<IllegalArgumentException> {
            Patient(graphQLPatientInput, session)
        }
        assertEquals(emptyList<Patient>(), patientRepository.findAll())
    }
}