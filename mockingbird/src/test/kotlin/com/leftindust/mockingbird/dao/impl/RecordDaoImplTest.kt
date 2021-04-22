package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.extensions.Authorization
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecordDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val recordRepository = mockk<HibernateRecordRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()

    @AfterEach
    internal fun tearDown() {
        confirmVerified(authorizer, recordRepository, patientRepository)
    }

    @Test
    fun getRecordByRecordId() {
        val mockkRecord = mockk<MediqRecord>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { recordRepository.getOne(1000) } returns mockkRecord

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordByRecordId(1000, mockk()) }

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            recordRepository.getOne(1000)
        }

        confirmVerified(mockkRecord)

        assertEquals(mockkRecord, result)
    }

    @Test
    fun getRecordsByPatientPid() {
        val mockkRecord = mockk<MediqRecord>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val mockkPatient = mockk<Patient> {
            every { id } returns 100
        }

        every { patientRepository.getOne(1000) } returns mockkPatient
        every { recordRepository.getAllByPatientId(100L) } returns listOf(mockkRecord)

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordsByPatientPid(1000, mockk()) }

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            patientRepository.getOne(1000)
            recordRepository.getAllByPatientId(100)
            mockkPatient.id
        }

        confirmVerified(mockkRecord, mockkPatient)

        assertEquals(listOf(mockkRecord), result)
    }
}