package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.NameInfoDao
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.external.firebase.UserFetcher
import org.springframework.beans.factory.annotation.Autowired

@GraphQLName("User")
data class GraphQLUser(
    val uid: String,
    val group: Group? = null,
    private val authContext: GraphQLAuthContext
) {
    constructor(mediqUser: MediqUser, graphQLAuthContext: GraphQLAuthContext) : this(
        uid = mediqUser.uniqueId,
        group = mediqUser.group?.let { Group(it) },
        authContext = graphQLAuthContext,
    )

    suspend fun names(@GraphQLIgnore @Autowired nameInfoDao: NameInfoDao): GraphQLNameInfo? {
        return nameInfoDao.findByUniqueId(uid, authContext.mediqAuthToken)?.let { GraphQLNameInfo(it) }
    }

    suspend fun isRegistered(@GraphQLIgnore @Autowired userDao: UserDao): Boolean {
        return userDao.findUserByUid(uid, authContext.mediqAuthToken) != null
    }

    suspend fun firebaseUserInfo(@GraphQLIgnore @Autowired userFetcher: UserFetcher): GraphQLFirebaseInfo =
        GraphQLFirebaseInfo(userFetcher.getUserInfo(uid, authContext.mediqAuthToken))

    suspend fun permissions(@GraphQLIgnore @Autowired authorizationDao: AuthorizationDao): GraphQLPermissions {
        return GraphQLPermissions(authorizationDao.getRolesForUserByUid(uid))
    }

    suspend fun hasPermission(
        @GraphQLIgnore @Autowired authorizationDao: AuthorizationDao,
        perm: GraphQLPermission
    ): Boolean {
        val action = Action(perm)
        return authorizationDao.getRolesForUserByUid(uid).any {
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
        val gid: ID,
        val name: String
    ) {
        constructor(group: MediqGroup) : this(
            gid = gqlID(group.id!!),
            name = group.name
        )
    }
}


