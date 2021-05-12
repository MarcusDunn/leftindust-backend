package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import org.springframework.stereotype.Component

@Component
class ClinicQuery(private val clinicDao: ClinicDao) : Query {
    suspend fun clinic(cid: ID, authContext: GraphQLAuthContext): GraphQLClinic {
        return clinicDao.getByCid(cid, authContext.mediqAuthToken).let { GraphQLClinic(it, it.id!!, authContext) }
    }
}