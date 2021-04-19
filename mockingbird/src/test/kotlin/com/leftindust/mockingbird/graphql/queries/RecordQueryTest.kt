package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.RecordDao
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecordQueryTest {
    private val recordDao = mockk<RecordDao>()

    @Test
    fun getRecord() {
        val mockkRecord = mockk<MediqRecord>(relaxed = true) {
            every { id } returns 1000
        }
        coEvery { recordDao.getRecordByRecordId(1000, any()) } returns mockkRecord

        val recordQuery = RecordQuery(recordDao)
        val mockkAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking { recordQuery.getRecord(gqlID(1000), mockkAuthContext) }

        assertEquals(GraphQLRecord(mockkRecord, mockkRecord.id!!, mockkAuthContext), result)
    }

    @Test
    fun getRecords() {
        val mockkRecord = mockk<MediqRecord>(relaxed = true) {
            every { id } returns 1000
        }
        coEvery { recordDao.getRecordsByPatientPid(2000, any()) } returns listOf(mockkRecord)

        val recordQuery = RecordQuery(recordDao)
        val mockkAuthContext = mockk<GraphQLAuthContext>() {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking { recordQuery.getRecords(gqlID(2000), mockkAuthContext) }

        assertEquals(listOf(GraphQLRecord(mockkRecord, mockkRecord.id!!, mockkAuthContext)), result)
    }
}