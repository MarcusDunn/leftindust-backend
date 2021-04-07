package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.springframework.stereotype.Component

@Component
class DoctorMutation(private val doctorDao: DoctorDao) : Mutation {
    suspend fun addDoctor(doctor: GraphQLDoctorInput, graphQLAuthContext: GraphQLAuthContext): GraphQLDoctor {
        return doctorDao
            .addDoctor(doctor, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLDoctor(it, it.id!!, graphQLAuthContext) }
    }
}