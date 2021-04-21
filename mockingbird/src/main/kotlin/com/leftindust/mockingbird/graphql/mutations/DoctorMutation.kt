package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.DoesNotExist
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.springframework.stereotype.Component

@Component
class DoctorMutation(private val doctorDao: DoctorDao, private val userDao: UserDao) : Mutation {
    suspend fun addDoctor(
        doctor: GraphQLDoctorInput,
        graphQLAuthContext: GraphQLAuthContext,
    ): GraphQLDoctor {
        val user = if (doctor.user != null) {
            when (val result = userDao.getUserByUid(doctor.user.uid, graphQLAuthContext.mediqAuthToken)) {
                is Success -> result.value
                is Failure -> when (result.reason) {
                    is DoesNotExist -> userDao.addUser(doctor.user, graphQLAuthContext.mediqAuthToken)
                    else -> throw GraphQLKotlinException(result.reason.toString())
                }
            }
        } else null

        return doctorDao
            .addDoctor(doctor, graphQLAuthContext.mediqAuthToken, user = user)
            .let { GraphQLDoctor(it, it.id!!, graphQLAuthContext) }
    }
}