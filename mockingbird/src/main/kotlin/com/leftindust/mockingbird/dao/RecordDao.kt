package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.MediqRecord

interface RecordDao {
    suspend fun getRecordByRecordId(rid: Long, requester: MediqToken): MediqRecord
    suspend fun getRecordsByPatientPid(pid: Long, requester: MediqToken): Collection<MediqRecord>
}
