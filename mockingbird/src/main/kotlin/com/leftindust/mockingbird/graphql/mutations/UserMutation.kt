package com.leftindust.mockingbird.graphql.mutations

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Mutation
import com.google.gson.JsonParser
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import org.springframework.stereotype.Component

@Component
class UserMutation(
    private val userDao: UserDao
) : Mutation {
    suspend fun setUserSettings(
        uid: ID,
        newSettings: GraphQLUser.Settings,
        graphQLAuthContext: GraphQLAuthContext
    ): GraphQLUser {
        val jsonSettings = JsonParser.parseString(newSettings.settings.json).asJsonObject

        val user = userDao
            .setUserSettingsByUid(uid.toString(), newSettings.version, jsonSettings, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
        return GraphQLUser(user, graphQLAuthContext)
    }

    suspend fun addUser(user: GraphQLUserInput, graphQLAuthContext: GraphQLAuthContext): GraphQLUser {
        return userDao
            .addUser(user, graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
            .let { GraphQLUser(it, graphQLAuthContext) }
    }
}