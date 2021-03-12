package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.extensions.CustomResult

interface DoctorDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason>
    suspend fun getByVisit(vid: Long?, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
    suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
}
