package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.server.operations.Mutation
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import org.springframework.stereotype.Component

@Component
class UserMutation(
    private val userDao: UserDao
) : Mutation {
    suspend fun addUser(user: GraphQLUserInput, graphQLAuthContext: GraphQLAuthContext): GraphQLUser {
        return userDao
            .addUser(user, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLUser(it, graphQLAuthContext) }
    }
}