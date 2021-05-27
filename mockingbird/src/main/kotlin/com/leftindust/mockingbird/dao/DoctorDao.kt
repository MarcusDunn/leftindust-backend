package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput

interface DoctorDao {
    suspend fun getByPatient(pid: GraphQLPatient.ID, requester: MediqToken): Collection<Doctor>
    suspend fun getByEvent(eid: GraphQLEvent.ID, requester: MediqToken): Collection<Doctor>
    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Doctor
    suspend fun addDoctor(doctor: GraphQLDoctorInput, requester: MediqToken, user: MediqUser? = null): Doctor
    suspend fun editDoctor(doctor: GraphQLDoctorEditInput, requester: MediqToken): Doctor
    suspend fun getByClinic(clinic: GraphQLClinic.ID, requester: MediqToken): Collection<Doctor>
    suspend fun getByUser(uid: String, requester: MediqToken): Doctor?
    suspend fun getMany(range: GraphQLRangeInput, requester: MediqToken): Collection<Doctor>
}