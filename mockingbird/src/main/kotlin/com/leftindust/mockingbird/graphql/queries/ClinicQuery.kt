package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.clinic.ReadClinicDao
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import org.springframework.stereotype.Component

@Component
class ClinicQuery(private val clinicDao: ReadClinicDao) : Query {
    suspend fun clinic(cid: GraphQLClinic.ID, authContext: GraphQLAuthContext): GraphQLClinic {
        val clinic = clinicDao.getByCid(cid, authContext.mediqAuthToken)
        return GraphQLClinic(clinic, authContext)
    }
}