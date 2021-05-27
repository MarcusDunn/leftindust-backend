package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ClinicDao
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.springframework.stereotype.Component

@Component
class ClinicMutation(private val clinicDao: ClinicDao) : Mutation {
    suspend fun addClinic(clinic: GraphQLClinicInput, authContext: GraphQLAuthContext): GraphQLClinic {
        return clinicDao
            .addClinic(clinic, authContext.mediqAuthToken)
            .let { GraphQLClinic(it, authContext) }
    }

    suspend fun editClinic(clinic: GraphQLClinicEditInput, authContext: GraphQLAuthContext): GraphQLClinic {
        return clinicDao
            .editClinic(clinic, authContext.mediqAuthToken)
            .let { GraphQLClinic(it, authContext) }
    }
}