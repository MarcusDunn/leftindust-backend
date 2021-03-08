package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.extensions.toInt
import com.leftindust.mediq.graphql.types.GraphQLDoctor
import org.springframework.stereotype.Component

@Component
class DoctorQuery(
    private val doctorDao: DoctorDao,
) : Query {
    suspend fun getDoctorsByPatient(
        pid: ID,
        authContext: GraphQLAuthContext
    ): List<GraphQLDoctor> {
        return doctorDao
            .getByPatient(pid.toInt(), authContext.mediqAuthToken)
            .getOrThrow()
            .map { GraphQLDoctor(it, authContext) }
    }

    suspend fun doctor(did: ID, authContext: GraphQLAuthContext): GraphQLDoctor {
        return doctorDao.getByDoctor(did.toInt(), authContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLDoctor(it, authContext) }
    }
}
