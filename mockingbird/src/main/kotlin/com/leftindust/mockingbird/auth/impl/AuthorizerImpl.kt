package com.leftindust.mockingbird.auth.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.Authorization
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * a firebase based implementation of Authorizer
 * @property authorizationDao handles all interaction with the user permissions stored in the DB
 */
@Service
internal class AuthorizerImpl(
    @Autowired private val authorizationDao: AuthorizationDao
) : Authorizer {
    val logger: Logger = LogManager.getLogger()

    override suspend fun getAuthorization(action: Action, user: MediqToken): Authorization {
        return logAuth(user, action) {
            if (authorizationDao.isAdmin(user.uid ?: return@logAuth Authorization.Denied)
            ) {
                Authorization.Allowed
            } else {
                (getRoles(user) ?: return@logAuth Authorization.Denied)
                    .map { it.action }
                    .any { it.isSuperset(action) }
                    .toAuthorization()
            }
        }
    }

    private suspend fun logAuth(
        user: MediqToken,
        action: Action,
        function: suspend () -> Authorization
    ): Authorization {
        val authorization = function()
        if (authorization == Authorization.Denied) {
            logger.warn("denied $user from $action")
        }
        return authorization
    }

    private suspend fun getRoles(user: MediqToken): List<AccessControlList>? {
        return user.uid?.let { uid ->
            authorizationDao
                .getRolesForUserByUid(uid)
                .getOrNull()
        }
    }

    private fun Boolean.toAuthorization() = if (this) Authorization.Allowed else Authorization.Denied
}
