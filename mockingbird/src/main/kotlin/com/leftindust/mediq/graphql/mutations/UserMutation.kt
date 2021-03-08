package com.leftindust.mediq.graphql.mutations

import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Mutation
import com.google.gson.JsonParser
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.UserDao
import com.leftindust.mediq.graphql.types.GraphQLJsonObject
import com.leftindust.mediq.graphql.types.GraphQLUser
import com.leftindust.mediq.graphql.types.input.GraphQLUserInput
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