package com.leftindust.mockingbird.dao.email

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Email
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPatient

interface ReadEmailDao {
    suspend fun getDoctorEmails(did: GraphQLDoctor.ID, mediqAuthToken: MediqToken): List<Email>
    suspend fun getEmergencyContactEmails(ecid: GraphQLEmergencyContact.ID, mediqAuthToken: MediqToken): List<Email>
    suspend fun getPatientEmails(pid: GraphQLPatient.ID, authContext: GraphQLAuthContext): List<Email>
}