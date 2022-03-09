package com.leftindust.mockingbird.dao.address

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Address
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLPatient

interface ReadAddressDao {
    suspend fun getDoctorAddresses(did: GraphQLDoctor.ID, mediqAuthToken: MediqToken): List<Address>
    suspend fun getPatientAddresses(pid: GraphQLPatient.ID, authContext: GraphQLAuthContext): List<Address>
}
