package com.leftindust.mockingbird.dao

import com.google.gson.JsonObject
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.RecordType
import com.leftindust.mockingbird.dao.entity.enums.Sex
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
internal class RecordDaoTest(
    @Autowired private val recordDao: RecordDao
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getRecordByRecordId() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val record = MediqRecord(
            rid = 0,
            patient = patient,
            creationDate = TimestampFaker(0).create(),
            type = RecordType.Blood,
            jsonBlob = JsonObject().apply {
                addProperty("person", "marcus")
            }.toString()
        )
        session.save(record)

        val result = runBlocking { recordDao.getRecordByRecordId(0, FakeAuth.Valid.Token) }

        assertEquals(record, result.unwrap())
    }

    @Test
    fun `getRecordByRecordId with no such record`() {
        val result = runBlocking { recordDao.getRecordByRecordId(0, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }

    @Test
    fun getRecordsByPatientPid() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long
        val record = MediqRecord(
            rid = 0,
            patient = patient,
            creationDate = TimestampFaker(0).create(),
            type = RecordType.Blood,
            jsonBlob = JsonObject().apply {
                addProperty("person", "marcus")
            }.toString()
        )
        session.save(record)

        val result = runBlocking { recordDao.getRecordsByPatientPid(patientID, FakeAuth.Valid.Token) }

        assertEquals(listOf(record), result.unwrap())
    }

    @Test
    fun `getRecordsByPatientPid with no such record`() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long

        val result = runBlocking { recordDao.getRecordsByPatientPid(patientID, FakeAuth.Valid.Token) }

        assertEquals(emptyList<MediqRecord>(), result.unwrap())
    }

    @Test
    fun `getRecordsByPatientPid with no such patient`() {
        val result = runBlocking { recordDao.getRecordsByPatientPid(0, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }
}