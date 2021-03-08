package com.leftindust.mediq.graphql.types

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.google.gson.JsonParser
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.AuthorizationDao
import com.leftindust.mediq.dao.DoesNotExist
import com.leftindust.mediq.dao.NoUidForUser
import com.leftindust.mediq.dao.UserDao
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.extensions.Failure
import com.leftindust.mediq.extensions.Success
import com.leftindust.mediq.external.firebase.UserFetcher
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("User")
data class GraphQLUser(
    val uid: String,
    val group: Group? = null,
    val settings: Settings? = null,
    private val authContext: GraphQLAuthContext
) {
    constructor(mediqUser: MediqUser, graphQLAuthContext: GraphQLAuthContext) : this(
        uid = mediqUser.uniqueId,
        group = mediqUser.group?.let { Group(it.name) },
        settings = Settings(
            version = mediqUser.settings.version,
            settings = GraphQLJsonObject(
                json = JsonParser.parseString(mediqUser.settings.settingsJSON).asJsonObject.toString()
            ),
        ),
        authContext = graphQLAuthContext,
    )

    suspend fun isRegistered(@GraphQLIgnore @Autowired userDao: UserDao): Boolean {
        return when (val result = userDao.getUserByUid(uid, authContext.mediqAuthToken)) {
            is Failure -> when (result.reason) {
                is DoesNotExist -> false
                else -> result.getOrThrow() // throw but with nice logging already handled in getOrThrow()
            }
            is Success -> true
        }
    }

    suspend fun firebaseUserInfo(@GraphQLIgnore @Autowired userFetcher: UserFetcher): GraphQLFirebaseInfo =
        GraphQLFirebaseInfo(userFetcher.getUserInfo(uid, authContext.mediqAuthToken).getOrThrow())

    suspend fun permissions(@GraphQLIgnore @Autowired authorizationDao: AuthorizationDao): GraphQLPermissions {
        return GraphQLPermissions(
            authorizationDao.getRolesForUserByUid(uid).getOrNull() ?: emptyList()
        ) //todo check if this is a security issue
    }

    suspend fun hasPermission(
        @GraphQLIgnore @Autowired authorizationDao: AuthorizationDao,
        perm: GraphQLPermission
    ): Boolean {
        val action = Action(perm)
        return authorizationDao.getRolesForUserByUid(uid).getOrThrow().any {
            it.action.isSuperset(
                action
            )
        }
    }

    data class Settings(
        val version: Int,
        val settings: GraphQLJsonObject
    )

    data class Group(
        val name: String
    )
}


