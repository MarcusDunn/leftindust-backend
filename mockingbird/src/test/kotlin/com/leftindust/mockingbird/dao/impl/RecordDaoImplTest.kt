package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.getOneOrNull
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecordDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val recordRepository = mockk<HibernateRecordRepository>()
    private val patientRepository = mockk<HibernatePatientRepository>()

    @Test
    fun getRecordByRecordId() {
        val mockkRecord = mockk<MediqRecord>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { recordRepository.getOneOrNull(1000) } returns mockkRecord

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordByRecordId(1000, mockk()) }

        assertEquals(mockkRecord, result)
    }

    @Test
    fun getRecordsByPatientPid() {
        val mockkRecord = mockk<MediqRecord>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getOneOrNull(1000) } returns mockk() {
            every { id } returns 100
        }
        every { recordRepository.getAllByPatientId(100L) } returns listOf(mockkRecord)

        val recordDaoImpl = RecordDaoImpl(authorizer, recordRepository, patientRepository)

        val result = runBlocking { recordDaoImpl.getRecordsByPatientPid(1000, mockk()) }

        assertEquals(listOf(mockkRecord), result)
    }
}