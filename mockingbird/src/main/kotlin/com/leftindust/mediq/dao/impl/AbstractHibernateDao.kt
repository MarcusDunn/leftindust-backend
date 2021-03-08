package com.leftindust.mediq.dao.impl

import com.leftindust.mediq.auth.Authorizer
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.auth.MediqToken
import com.leftindust.mediq.dao.DoesNotExist
import com.leftindust.mediq.dao.NotAuthorized
import com.leftindust.mediq.dao.OrmFailureReason
import com.leftindust.mediq.dao.Tables
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.extensions.*
import org.apache.logging.log4j.LogManager

abstract class AbstractHibernateDao(private val authorizer: Authorizer) {
    suspend fun <T> Action.getAuthorization(
        requester: MediqToken,
        onAuthorized: () -> CustomResult<T, OrmFailureReason>
    ): CustomResult<T, OrmFailureReason> {
        return when (authorizer.getAuthorization(this, requester)) {
            Authorization.Allowed -> onAuthorized()
            Authorization.Denied -> Failure(NotAuthorized(requester, "no permission to ${this.permissionType.name} to ${this.referencedTableName}"))
        }
    }

    suspend infix fun MediqToken.has(actions: List<Action>): Boolean {
        return actions.all { authorizer.getAuthorization(it, this).isAllowed() }
    }

    suspend infix fun MediqToken.can(actions: Action): Boolean {
        return authorizer.getAuthorization(actions, this).isAllowed()
    }


    suspend infix fun MediqToken.can(action: Pair<Crud, Tables>): Boolean {
        return authorizer.getAuthorization(Action(action), this).isAllowed()
    }

    /**
     * Authenticates, returning a Failure<NotAuthorized> if it fails then executes the [onAllowed] if [onAllowed]
     * returns null than this transforms it into a Failure<DoesNotExist>, behavior beyond this merits a custom function
     * @returns a [CustomResult] with the success variant typed as the return type of [onAllowed]
     * @param onAllowed a function to be executed and transformed into either Success<T> if it returns T or
     * Failure<DoesNotExist> if it returns null
     * @param action the action required to execute [onAllowed]
     * @param requester the token of the user requesting to execute [onAllowed]
     */
    suspend inline fun <T> authenticateAndThen(
        requester: MediqToken,
        action: Pair<Crud, Tables>,
        onAllowed: () -> T?
    ): CustomResult<T, OrmFailureReason> {
        return if (requester can action) {
            val ret = onAllowed() ?: return Failure(DoesNotExist()).also {
                LogManager.getLogger().error("attempt to get a something nonexistent")
            }
            Success(ret)
        } else {
            LogManager.getLogger().error("unauthorized attempt to $action")
            Failure(NotAuthorized(requester, "cannot $action"))
        }
    }
}