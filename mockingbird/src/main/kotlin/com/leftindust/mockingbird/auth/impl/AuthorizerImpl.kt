package com.leftindust.mockingbird.auth.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.Authorization
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
    override suspend fun getAuthorization(action: Action, user: MediqToken): Authorization {
        if (authorizationDao.isAdmin(user.uid ?: return Authorization.Denied)) return Authorization.Allowed
        return (getRoles(user) ?: return Authorization.Denied)
            .map { it.action }
            .any { it.isSuperset(action) }
            .toAuthorization()
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
