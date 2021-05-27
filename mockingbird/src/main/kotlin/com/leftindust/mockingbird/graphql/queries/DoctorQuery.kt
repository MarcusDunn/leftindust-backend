package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.extensions.toLong
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class DoctorQuery(
    private val doctorDao: DoctorDao,
) : Query {
    @GraphQLDescription("only pass one variable")
    suspend fun doctors(
        dids: List<GraphQLDoctor.ID>? = null,
        pid: GraphQLPatient.ID? = null,
        range: GraphQLRangeInput? = null,
        authContext: GraphQLAuthContext
    ): List<GraphQLDoctor> {
        return when {
            dids != null -> dids
                .map { doctorDao.getByDoctor(it, authContext.mediqAuthToken) }
            pid != null -> doctorDao
                .getByPatient(pid, authContext.mediqAuthToken)
            range != null -> {
                doctorDao.getMany(range, authContext.mediqAuthToken)
            }
            else -> throw IllegalArgumentException("invalid argument combination to doctors")
        }.map { GraphQLDoctor(it, authContext) }
    }
}
