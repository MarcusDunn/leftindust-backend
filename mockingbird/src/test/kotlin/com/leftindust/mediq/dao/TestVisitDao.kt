package com.leftindust.mediq.dao

import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mediq.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mediq.dao.impl.repository.HibernateVisitRepository
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.DoctorFaker
import com.leftindust.mediq.helper.mocker.PatientFaker
import com.leftindust.mediq.helper.mocker.TimestampFaker
import com.leftindust.mediq.helper.mocker.VisitFaker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@SpringBootTest
@Transactional
class TestVisitDao(
    @Autowired private val dao: VisitDao,
    @Autowired private val visitRepository: HibernateVisitRepository,
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
) {

    val fakeAuthToken = FakeAuth.Valid.Token

    @Test
    internal fun `test dao autowire`() {
        dao.hashCode()
    }

    @Test
    internal fun `test getVisit return value`() {
        val timestampFaker = TimestampFaker(101)
        val (timeOfVisit, timeBooked) = listOf(timestampFaker(), timestampFaker()).sortedBy { it.nanos }
        val visit = Visit(
            timeBooked = timeBooked,
            timeOfVisit = timeOfVisit,
            patient = PatientFaker(101).create(),
            doctor = DoctorFaker(101).create(),
            icdFoundationCode = FoundationIcdCode("1"),

            )

        val vid = saveVisitWithCascade(visit)

        val result = runBlocking { dao.getVisitByVid(vid, fakeAuthToken).unwrap() }

        assertEquals(visit, result)
    }

    @Test
    internal fun `test getVisit returns doesNotExist`() {
        val result = runBlocking { dao.getVisitByVid(0, fakeAuthToken).unwrapFailure() }
        assert(result is DoesNotExist)
    }

    @Test
    // TODO: 2021-02-09 see if can be sped up
    internal fun `test getVisitsForPatient`() {
        val visitFaker = VisitFaker(101)
        val patient = PatientFaker(102).create()

        for (i in 0 until 20) {
            val visit = visitFaker.create()
            visit.patient = patient
            saveVisitWithCascade(visit)
        }

        val result = runBlocking { dao.getVisitsForPatientPid(patient.pid, fakeAuthToken).unwrap() }

        assertEquals(result.size, 20)
    }

    private fun saveVisitWithCascade(visit: Visit): Long {
        patientRepository.save(visit.patient)
        doctorRepository.save(visit.doctor)
        return visitRepository.save(visit).id!!
    }
}
