package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.RecordDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.GraphQLRecord
import com.leftindust.mockingbird.graphql.types.input.GraphQLRecordInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class RecordDaoImpl(
    @Autowired authorizer: Authorizer,
    @Autowired private val recordRepository: HibernateRecordRepository,
    @Autowired private val patientRepository: HibernatePatientRepository,
) : AbstractHibernateDao(authorizer), RecordDao {
    override suspend fun getRecordByRecordId(rid: GraphQLRecord.ID, requester: MediqToken): MediqRecord {
        return if (requester can (Crud.READ to Tables.Record)) {
            withContext(Dispatchers.IO) {
                recordRepository.getById(rid.id)
            }
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Record)
        }
    }

    override suspend fun getRecordsByPatientPid(
        pid: GraphQLPatient.ID,
        requester: MediqToken
    ): Collection<MediqRecord> {
        val readRecords = Crud.READ to Tables.Record
        return if (requester can readRecords) {
            withContext(Dispatchers.IO) {
                val patient = patientRepository.getById(pid.id)
                recordRepository.getAllByPatientId(patient.id!!)
            }
        } else {
            throw NotAuthorizedException(requester, readRecords)
        }
    }

    override suspend fun addRecord(record: GraphQLRecordInput, requester: MediqToken): MediqRecord {
        val createRecords = Crud.CREATE to Tables.Record
        return if (requester can createRecords) {
            withContext(Dispatchers.IO) {
                val patient = patientRepository.getById(record.patient.id)
                val recordEntity = MediqRecord(record, patient)
                recordRepository.save(recordEntity)
            }
        } else {
            throw NotAuthorizedException(requester, createRecords)
        }
    }
}