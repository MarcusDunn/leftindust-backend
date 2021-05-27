package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.RecordDao
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class RecordQueryTest {
    private val recordDao = mockk<RecordDao>()

    @Test
    fun getRecord() {
        val recordID = UUID.randomUUID()

        val mockkRecord = mockk<MediqRecord>(relaxed = true) {
            every { id } returns recordID
        }
        coEvery { recordDao.getRecordByRecordId(GraphQLRecord.ID(recordID), any()) } returns mockkRecord

        val recordQuery = RecordQuery(recordDao)
        val mockkAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking { recordQuery.getRecord(
            GraphQLRecord.ID(recordID), mockkAuthContext) }

        assertEquals(GraphQLRecord(mockkRecord, mockkAuthContext), result)
    }

    @Test
    fun getRecords() {
        val recordID = UUID.randomUUID()
        val patientID = UUID.randomUUID()

        val mockkRecord = mockk<MediqRecord>(relaxed = true) {
            every { id } returns recordID
        }
        coEvery { recordDao.getRecordsByPatientPid(GraphQLPatient.ID(patientID), any()) } returns listOf(mockkRecord)

        val recordQuery = RecordQuery(recordDao)
        val mockkAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking { recordQuery.getRecords(GraphQLPatient.ID(patientID), mockkAuthContext) }

        assertEquals(listOf(GraphQLRecord(mockkRecord, mockkAuthContext)), result)
    }
}