package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorDao {
    suspend fun getByPatient(pid: Long, requester: MediqToken): Collection<Doctor>
    suspend fun getByEvent(eid: Long, requester: MediqToken): Collection<Doctor>
    suspend fun getByDoctor(did: Long, requester: MediqToken): Doctor
    suspend fun addDoctor(doctor: GraphQLDoctorInput, requester: MediqToken, user: MediqUser? = null): Doctor
    suspend fun editDoctor(doctor: GraphQLDoctorEditInput, requester: MediqToken): Doctor
}