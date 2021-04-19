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
    override suspend fun getRecordByRecordId(
        rid: Long,
        requester: MediqToken
    ): MediqRecord {
        return if (requester can (Crud.READ to Tables.Record)) {
            recordRepository.getOne(rid)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Record)
        }
    }

    override suspend fun getRecordsByPatientPid(
        pid: Long,
        requester: MediqToken
    ): Collection<MediqRecord> {
        if (requester can (Crud.READ to Tables.Record)) {
            val patient = patientRepository.getOne(pid)
            return recordRepository.getAllByPatientId(patient.id!!)
        } else {
            throw NotAuthorizedException(requester, Crud.READ to Tables.Record)
        }
    }
}