package com.leftindust.mediq.dao

import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.entity.EmergencyContact
import com.leftindust.mediq.extensions.CustomResult

interface ContactDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): CustomResult<List<EmergencyContact>, OrmFailureReason>
}
