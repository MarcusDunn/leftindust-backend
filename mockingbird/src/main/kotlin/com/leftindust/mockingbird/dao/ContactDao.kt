package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPatient

interface ContactDao {
    suspend fun getByPatient(pid: GraphQLPatient.ID, requester: MediqToken): Collection<EmergencyContact>
}
