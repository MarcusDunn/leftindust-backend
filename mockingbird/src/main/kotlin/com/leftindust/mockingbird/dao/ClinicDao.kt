package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput

interface ClinicDao {
    suspend fun addClinic(clinic: GraphQLClinicInput, requester: MediqToken): Clinic
    suspend fun editClinic(clinic: GraphQLClinicEditInput, requester: MediqToken): Clinic
    suspend fun getByDoctor(did: GraphQLDoctor.ID, requester: MediqToken): Collection<Clinic>
    suspend fun getByCid(cid: GraphQLClinic.ID, requester: MediqToken): Clinic
}
