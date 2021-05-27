package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

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

        val recordId = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { recordRepository.getById(recordId) } returns mockkRecord

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordByRecordId(GraphQLRecord.ID(recordId), mockk()) }

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            recordRepository.getById(recordId)
        }

        confirmVerified(mockkRecord)

        assertEquals(mockkRecord, result)
    }

    @Test
    fun getRecordsByPatientPid() {
        val mockkRecord = mockk<MediqRecord>()
        val patientID = UUID.randomUUID()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val mockkPatient = mockk<Patient> {
            every { id } returns patientID
        }

        every { patientRepository.getById(patientID) } returns mockkPatient
        every { recordRepository.getAllByPatientId(patientID) } returns listOf(mockkRecord)

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordsByPatientPid(GraphQLPatient.ID(patientID), mockk()) }

        coVerifyAll {
            authorizer.getAuthorization(any(), any())
            patientRepository.getById(patientID)
            recordRepository.getAllByPatientId(patientID)
            mockkPatient.id
        }

        confirmVerified(mockkRecord, mockkPatient)

        assertEquals(listOf(mockkRecord), result)
    }
}