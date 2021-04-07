package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLDoctorInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DoctorMutation(private val doctorDao: DoctorDao) : Mutation {
    suspend fun addDoctor(
        doctor: GraphQLDoctorInput,
        graphQLAuthContext: GraphQLAuthContext,
        @GraphQLIgnore @Autowired userDao: UserDao
    ): GraphQLDoctor {
        val user = doctor.user?.let {
            userDao.getUserByUid(it.uid, graphQLAuthContext.mediqAuthToken).getOrNull()
                ?: userDao.addUser(it, graphQLAuthContext.mediqAuthToken).getOrThrow()
        }
        return doctorDao
            .addDoctor(doctor, graphQLAuthContext.mediqAuthToken, user = user)
            .getOrThrow()
            .let { GraphQLDoctor(it, it.id!!, graphQLAuthContext) }
    }
}