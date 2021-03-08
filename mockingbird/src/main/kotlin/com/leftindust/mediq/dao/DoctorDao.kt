package com.leftindust.mediq.dao

import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.extensions.CustomResult

interface DoctorDao {
    suspend fun getByPatient(pid: Int, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason>
    suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
    suspend fun getByDoctor(did: Int, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
}
