package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import org.springframework.stereotype.Component

@Component
class DoctorQuery(
    private val doctorDao: DoctorDao,
) : Query {
    suspend fun doctors(
        dids: List<ID>? = null,
        pid: ID? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLDoctor> {
        return when {
            dids != null -> dids
                .map { doctorDao.getByDoctor(it.toLong(), authContext.mediqAuthToken) }
                .map { it.getOrThrow() }
            else -> throw IllegalArgumentException("invalid argument combination to doctors")
        }.map { GraphQLDoctor(it, it.id!!, authContext) }
    }

    suspend fun getDoctorsByPatient(pid: ID, authContext: GraphQLAuthContext): List<GraphQLDoctor> {
        return doctorDao
            .getByPatient(pid.toLong(), authContext.mediqAuthToken)
            .getOrThrow()
            .map { GraphQLDoctor(it, it.id!!, authContext) } // safe nn assert as we just got it from DB
    }

    suspend fun doctor(did: ID, authContext: GraphQLAuthContext): GraphQLDoctor {
        return doctorDao.getByDoctor(did.toLong(), authContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLDoctor(it, it.id!!, authContext) }  // safe nn assert as we just got it from DB
    }
}
