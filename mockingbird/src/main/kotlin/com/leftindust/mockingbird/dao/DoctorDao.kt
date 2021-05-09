package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput

interface DoctorDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<Doctor>
    suspend fun getByEvent(eid: Long, requester: MediqToken): Collection<Doctor>
    suspend fun getByDoctor(did: Long, requester: MediqToken): Doctor
    suspend fun addDoctor(doctor: GraphQLDoctorInput, requester: MediqToken, user: MediqUser? = null): Doctor
    suspend fun editDoctor(doctor: GraphQLDoctorEditInput, requester: MediqToken): Doctor
    suspend fun getByClinic(clinic: ID, requester: MediqToken): Collection<Doctor>
    suspend fun getByUser(uid: String, requester: MediqToken): Doctor?
}