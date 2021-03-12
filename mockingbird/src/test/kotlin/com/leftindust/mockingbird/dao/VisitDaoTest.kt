package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.TimestampFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@SpringBootTest
@Transactional
internal class VisitDaoTest(
    @Autowired private val visitDao: VisitDao
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession


    @Test
    fun getVisitsForPatientPid() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "shervershani",
        )
        session.save(doctor)
        val visit = Visit(
            doctor = doctor,
            patient = patient,
            timeBooked = TimestampFaker(0).create(),
            timeOfVisit = TimestampFaker(1).create(),
            icdFoundationCode = FoundationIcdCode("1"),
            )
        session.save(visit)

        val result = runBlocking { visitDao.getVisitsForPatientPid(patient.id!!, FakeAuth.Valid.Token) }

        assertEquals(listOf(visit), result.unwrap())
    }

    @Test
    fun `getVisitsForPatientPid with no such visit`() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking { visitDao.getVisitsForPatientPid(patient.id!!, FakeAuth.Valid.Token) }

        assertEquals(emptyList<Visit>(), result.unwrap())
    }

    @Test
    fun getVisitByVid() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "shervershani",
        )
        session.save(doctor)
        val visit = Visit(
            doctor = doctor,
            patient = patient,
            timeBooked = TimestampFaker(0).create(),
            timeOfVisit = TimestampFaker(1).create(),
            icdFoundationCode = FoundationIcdCode("1"),
            )
        val vid = session.save(visit) as Long

        val result = runBlocking { visitDao.getVisitByVid(vid, FakeAuth.Valid.Token) }

        assertEquals(visit, result.unwrap())
    }

    @Test
    fun `getVisitByVid with no such visit`() {
        val result = runBlocking { visitDao.getVisitByVid(0, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }

    @Test
    fun getVisitsByDoctor() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "shervershani",
        )
        val doctorID = session.save(doctor) as Long
        val visit = Visit(
            doctor = doctor,
            patient = patient,
            timeBooked = TimestampFaker(0).create(),
            timeOfVisit = TimestampFaker(1).create(),
            icdFoundationCode = FoundationIcdCode("1"),
            )
        session.save(visit)

        val result = runBlocking { visitDao.getVisitsByDoctor(doctorID, FakeAuth.Valid.Token) }

        assertEquals(listOf(visit), result.unwrap())
    }


    @Test
    fun `getVisitsByDoctor with no such visit`() {
        val result = runBlocking { visitDao.getVisitsByDoctor(-1, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }
}