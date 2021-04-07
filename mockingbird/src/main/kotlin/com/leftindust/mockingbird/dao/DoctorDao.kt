package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.CustomResult
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput

interface DoctorDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): CustomResult<List<Doctor>, OrmFailureReason>
    suspend fun getByVisit(vid: Long, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
    suspend fun getByDoctor(did: Long, requester: MediqToken): CustomResult<Doctor, OrmFailureReason>
    suspend fun addDoctor(doctor: GraphQLDoctorInput, requester: MediqToken, user: MediqUser? = null): CustomResult<Doctor, OrmFailureReason>
}