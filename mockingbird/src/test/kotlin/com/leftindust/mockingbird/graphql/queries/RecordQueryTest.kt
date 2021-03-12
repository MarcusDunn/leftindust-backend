package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.PatientFaker
import com.leftindust.mockingbird.helper.mocker.RecordFaker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@Transactional
class RecordQueryTest(
    @Autowired private val recordQuery: RecordQuery,
    @Autowired private val hibernateRecordRepository: HibernateRecordRepository,
    @Autowired private val hibernatePatientRepository: HibernatePatientRepository,
) {
    private final val fakeAuthContext = FakeAuth.Valid.Context

    @Test
    internal fun `test autowire`() {
        recordQuery.hashCode()
    }

    @Test
    internal fun `test get record return value`() {
        val record = RecordFaker(102).create()
        hibernateRecordRepository.save(record)

        val result = runBlocking { recordQuery.getRecord(ID(record.rid.toString()), fakeAuthContext) }

        assertEquals(graphQLRecord(record), result)
    }

    @Test
    internal fun `test records by patient return value`() {
        val recordFaker = RecordFaker(102)
        val patient = PatientFaker(102).create()
        hibernatePatientRepository.save(patient)
        val record1 = recordFaker().apply {
            this.patient = patient
        }
        val record2 = recordFaker().apply {
            this.patient = patient
        }
        hibernateRecordRepository.save(record1)
        hibernateRecordRepository.save(record2)

        val result = runBlocking { recordQuery.getRecords(gqlID(patient.id!!), fakeAuthContext) }

        assertEquals(result, listOf(graphQLRecord(record1), graphQLRecord(record2)))
    }

    private fun graphQLRecord(record: MediqRecord) = GraphQLRecord(record, fakeAuthContext)

}