package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput

interface ClinicDao {
    suspend fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic
    suspend fun editClinic(clinic: GraphQLClinicEditInput, requester: MediqToken): Clinic
    suspend fun getByDoctor(doctor: ID, requester: MediqToken): Collection<Clinic>
    suspend fun getByCid(cid: ID, requester: MediqToken): Clinic
}
