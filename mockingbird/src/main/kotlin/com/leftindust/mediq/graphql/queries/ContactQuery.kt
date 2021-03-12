package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.ContactDao
import com.leftindust.mediq.extensions.toLong
import com.leftindust.mediq.graphql.types.GraphQLEmergencyContact
import com.leftindust.mediq.graphql.types.GraphQLPerson
import org.springframework.stereotype.Component

@Component
class ContactQuery(private val contactDao: ContactDao) : Query {
    @Throws(GraphQLKotlinException::class)
    suspend fun getContactsByPatient(pid: ID, authContext: GraphQLAuthContext): List<GraphQLPerson> {
        return contactDao.getByPatient(pid.toLong(), authContext.mediqAuthToken)
            .getOrThrow()
            .map { GraphQLEmergencyContact(it, authContext) }
    }
}