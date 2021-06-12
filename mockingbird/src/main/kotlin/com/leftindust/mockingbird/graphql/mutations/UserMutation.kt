package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import org.springframework.stereotype.Component
import java.security.PrivateKey

@Component
class UserMutation(
    private val userDao: UserDao,
) : Mutation {
    suspend fun addUser(user: GraphQLUserInput, graphQLAuthContext: GraphQLAuthContext): GraphQLUser {
        return userDao
            .addUser(user, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLUser(it, graphQLAuthContext) }
    }

    suspend fun editUser(user: GraphQLUserEditInput, graphQLAuthContext: GraphQLAuthContext): GraphQLUser {
        return userDao
            .updateUser(user, graphQLAuthContext.mediqAuthToken)
            .let { GraphQLUser(it, graphQLAuthContext) }
    }
}