package com.leftindust.mediq.dao

import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.DoctorPatientFaker
import com.leftindust.mediq.helper.mocker.PatientFaker
import com.leftindust.mediq.helper.mocker.TimestampFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@Transactional
@SpringBootTest
internal class DoctorDaoTest(
    @Autowired private val doctorDao: DoctorDao
) {

    @Autowired
    lateinit var sessionFactory: SessionFactory

    private val session: Session
        get() = sessionFactory.currentSession

    @Test
    // TODO: 2021-02-09 speed up
    fun getByPatient() {
        val patient = PatientFaker(0).create()
        session.save(patient)
        patient.doctors = DoctorPatientFaker(0).run {
            listOf(create(), create())
                .onEach { it.patient = patient }
                .onEach { session.save(it.doctor); session.save(it) }
                .toSet()
        }

        val result = runBlocking { doctorDao.getByPatient(patient.pid, FakeAuth.Valid.Token) }

        assertEquals(result.unwrap().toSet(), patient.doctors.map { it.doctor }.toSet())
    }

    @Test
    fun `getByPatient with non-existing Patient`() {
        // find some way to guarantee this doesnt exist by fluke
        val result = runBlocking { doctorDao.getByPatient(-1, FakeAuth.Valid.Token) }
        assert(result.unwrapFailure() is DoesNotExist) { result }
    }

    @Test
    fun getByVisit() {
        val patient = Patient(
            pid = 12,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Female,
        )
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Shervershani",
        )
        session.save(patient)
        session.save(doctor)
        val visit = Visit(
            patient = patient,
            doctor = doctor,
            timeBooked = TimestampFaker(1).create(),
            timeOfVisit = TimestampFaker(2).create(),
            icdFoundationCode = FoundationIcdCode("1"),
        )
        val vid = session.save(visit) as Long

        val result = runBlocking { doctorDao.getByVisit(vid, FakeAuth.Valid.Token).unwrap() }

        assertEquals(result, visit.doctor)
    }

    @Test
    fun `getByVisit with non-existing Visit`() {
        val result = runBlocking { doctorDao.getByVisit(-1, FakeAuth.Valid.Token).unwrapFailure() }
        assert(result is DoesNotExist) { result }
    }
}