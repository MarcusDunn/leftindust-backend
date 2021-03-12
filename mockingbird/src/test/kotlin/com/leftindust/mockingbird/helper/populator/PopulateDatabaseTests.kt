package com.leftindust.mockingbird.helper.populator

import com.leftindust.mockingbird.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mockingbird.helper.mocker.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hibernate.SessionFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

/*
    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*
These are an example of very bad code, these are disabled as they are more or less used as scripts to populate the
database at the frontends behest. Tests should not look like this, they should have no effects, these are not tests,
treat them as scripts that have access to all the spring goodies.
    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*    *WARNING*
 */
@SpringBootTest
class PopulateDatabaseTests(
    @Autowired private val hibernateVisitRepository: HibernateVisitRepository,
    @Autowired private val sessionFactory: SessionFactory,
) {
    private val logger: Logger = LogManager.getLogger()

    private final val contactFaker = ContactFaker(8)
    private val patientFaker = PatientFaker(4, contactFaker = contactFaker)
    private final val doctorPatentFaker = DoctorPatientFaker(5)
    private final val visitFaker = VisitFaker(6, doctorPatentFaker)
    private final val doctorFaker = DoctorFaker(7)


    @BeforeEach
    @AfterEach
    internal fun warn() {
        logger.error(
            """These are an example of very bad code, these are disabled as they are more or less used as scripts to populate the
database at the frontends behest. Tests should not look like this, they should have no effects, these are not tests,
treat them as scripts that have access to all the spring goodies."""
        )
    }

    @Test
    @Disabled("adds to database, used as a one-off")
    internal fun `populate database`() {
        val rand = Random.Default
        val session = sessionFactory.openSession()
        try {
            repeat(1_000) {
                val doctor = doctorFaker()
                val patient = patientFaker()
                session.save(doctor)
                session.save(patient)
                for (i in 0..rand.nextInt(8)) {
                    val visit = visitFaker().apply {
                        this.patient = patient
                        this.doctor = doctor
                    }
                    hibernateVisitRepository.save(visit)
                }
            }
        } catch (e: Exception) {
            session.transaction.rollback()
            throw e
        } finally {
            session.close()
        }

    }
}