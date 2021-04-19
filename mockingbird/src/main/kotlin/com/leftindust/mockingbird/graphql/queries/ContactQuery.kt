package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPerson
import org.springframework.stereotype.Component

@Component
class ContactQuery(
    private val contactDao: ContactDao
) : Query {
    @Throws(GraphQLKotlinException::class)
    suspend fun getContactsByPatient(pid: ID, authContext: GraphQLAuthContext): List<GraphQLPerson> {
        return contactDao.getByPatient(pid.toLong(), authContext.mediqAuthToken)
            .map { GraphQLEmergencyContact(it, authContext) }
    }
}