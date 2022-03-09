package com.leftindust.mockingbird.dao.phone

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Phone
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPatient

interface ReadPhoneDao {
    suspend fun getDoctorPhones(did: GraphQLDoctor.ID, mediqAuthToken: MediqToken): List<Phone>
    suspend fun getEmergencyContactPhones(ecid: GraphQLEmergencyContact.ID, mediqAuthToken: MediqToken): List<Phone>
    suspend fun getPatientPhones(pid: GraphQLPatient.ID, authContext: GraphQLAuthContext): List<Phone>
}
