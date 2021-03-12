package com.leftindust.mediq.dao

import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.extensions.CustomResult

interface DoctorDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason>
    suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
    suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
}
