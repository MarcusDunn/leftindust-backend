package com.leftindust.mockingbird.dao.email.impl

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.email.ReadEmailDao
import com.leftindust.mockingbird.dao.entity.Email
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class ReadEmailDaoImpl : ReadEmailDao {
    override suspend fun getDoctorEmails(did: GraphQLDoctor.ID, mediqAuthToken: MediqToken): List<Email> {
        TODO("Not yet implemented")
    }

    override suspend fun getEmergencyContactEmails(
        ecid: GraphQLEmergencyContact.ID,
        mediqAuthToken: MediqToken
    ): List<Email> {
        TODO("Not yet implemented")
    }

    override suspend fun getPatientEmails(pid: GraphQLPatient.ID, authContext: GraphQLAuthContext): List<Email> {
        TODO("Not yet implemented")
    }
}