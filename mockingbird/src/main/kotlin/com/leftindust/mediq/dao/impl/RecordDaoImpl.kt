package com.leftindust.mediq.dao.impl

import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.*
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.MediqRecord
import com.leftindust.mediq.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mediq.dao.impl.repository.HibernateRecordRepository
import com.leftindust.mediq.extensions.CustomResult
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
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
        rid: Int,
        requester: MediqToken
    ): CustomResult<MediqRecord, OrmFailureReason> {
        val readRecords = Action(Crud.READ to Tables.Record)
        return if (requester can readRecords) {
            val record = recordRepository.getByRid(rid) ?: return Failure(DoesNotExist())
            Success(record)
        } else {
            Failure(NotAuthorized(requester))
        }
    }

    override suspend fun getRecordsByPatientPid(
        pid: Int,
        requester: MediqToken
    ): CustomResult<List<MediqRecord>, OrmFailureReason> {
        return authenticateAndThen(requester, Crud.READ to Tables.Record) {
            val patient = patientRepository.getPatientByPid(pid)
                ?: return Failure(DoesNotExist("patient with pid $pid was not found"))
            recordRepository.getAllByPatientId(patient.id!!.toLong())
        }
    }
}