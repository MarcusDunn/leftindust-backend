package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.*
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.getOneOrNull
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
    ): CustomResult<MediqRecord, OrmFailureReason> {
        val readRecords = Action(Crud.READ to Tables.Record)
        return if (requester can readRecords) {
            val record = recordRepository.getOneOrNull(rid) ?: return Failure(DoesNotExist())
            Success(record)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getRecordsByPatientPid(
        pid: Long,
        requester: MediqToken
    ): CustomResult<List<MediqRecord>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Record) {
            val patient = patientRepository.getOneOrNull(pid)
                ?: return Failure(DoesNotExist("patient with pid $pid was not found"))
            recordRepository.getAllByPatientId(patient.id!!.toLong())
        }
    }
}