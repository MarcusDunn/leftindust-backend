package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqRecord
import com.leftindust.mockingbird.extensions.CustomResult

interface RecordDao {
    suspend fun getRecordByRecordId(rid: Int, requester: MediqToken): CustomResult<MediqRecord, OrmFailureReason>
    suspend fun getRecordsByPatientPid(pid: Long, requester: MediqToken): CustomResult<List<MediqRecord>, OrmFailureReason>
}
