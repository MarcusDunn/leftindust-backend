package com.leftindust.mockingbird.auth.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.Authorization
import org.apache.logging.log4j.LogManager
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
        return (authorizationDao
            .getRolesForUserByUid(user.uid ?: return Authorization.Denied)
            .getOrNull() ?: return Authorization.Denied)
            .map { it.action }
            .contains(action)
            .toAuthorization()
    }

    private fun Boolean.toAuthorization() = if (this) Authorization.Allowed else Authorization.Denied
}
