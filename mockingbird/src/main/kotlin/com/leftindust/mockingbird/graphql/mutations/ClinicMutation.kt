package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.clinic.CreateClinicDao
import com.leftindust.mockingbird.dao.clinic.UpdateClinicDao
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import org.springframework.stereotype.Component

@Component
class ClinicMutation(private val createClinicDao: CreateClinicDao, private val updateClinicDao: UpdateClinicDao) : Mutation {
    suspend fun addClinic(clinic: GraphQLClinicInput, authContext: GraphQLAuthContext): GraphQLClinic {
        return createClinicDao
            .addClinic(clinic, authContext.mediqAuthToken)
            .let { GraphQLClinic(it, authContext) }
    }

    suspend fun editClinic(clinic: GraphQLClinicEditInput, authContext: GraphQLAuthContext): GraphQLClinic {
        return updateClinicDao
            .editClinic(clinic, authContext.mediqAuthToken)
            .let { GraphQLClinic(it, authContext) }
    }
}