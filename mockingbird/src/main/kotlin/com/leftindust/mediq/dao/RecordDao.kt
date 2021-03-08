package com.leftindust.mediq.dao

import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.entity.MediqRecord
import com.leftindust.mediq.extensions.CustomResult

interface RecordDao {
    suspend fun getRecordByRecordId(rid: Int, requester: MediqToken): CustomResult<MediqRecord, OrmFailureReason>
    suspend fun getRecordsByPatientPid(pid: Int, requester: MediqToken): CustomResult<List<MediqRecord>, OrmFailureReason>
}
