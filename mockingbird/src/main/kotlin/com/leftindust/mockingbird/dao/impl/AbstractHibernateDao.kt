package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.DoesNotExist
import com.leftindust.mockingbird.dao.NotAuthorized
import com.leftindust.mockingbird.dao.OrmFailureReason
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.extensions.*
import com.leftindust.mockingbird.graphql.types.examples.GraphQLExample
import org.apache.logging.log4j.LogManager
import javax.persistence.EntityManager

abstract class AbstractHibernateDao(private val authorizer: Authorizer) {
    suspend fun <T> Action.getAuthorization(
        requester: MediqToken,
        onAuthorized: () -> CustomResult<T, OrmFailureReason>
    ): CustomResult<T, OrmFailureReason> {
        return when (authorizer.getAuthorization(this, requester)) {
            Authorization.Allowed -> onAuthorized()
            Authorization.Denied -> Failure(
                NotAuthorized(
                    requester,
                    "no permission to ${this.permissionType.name} to ${this.referencedTableName}"
                )
            )
        }
    }

    inline fun <reified T, EX : GraphQLExample<T>> searchByGqlExample(
        entityManager: EntityManager,
        example: EX,
        strict: Boolean
    ): Success<MutableList<T>> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(T::class.java)
        val itemRoot = criteriaQuery.from(T::class.java)
        val arrayOfPredicates = example.toPredicate(criteriaBuilder, itemRoot).toTypedArray()
        val finalPredicate = if (strict) {
            criteriaBuilder.and(*arrayOfPredicates)
        } else {
            criteriaBuilder.or(*arrayOfPredicates)
        }
        criteriaQuery.select(itemRoot).where(finalPredicate)

        return Success(entityManager.createQuery(criteriaQuery).resultList)
    }

    suspend infix fun MediqToken.has(actions: List<Action>): Boolean {
        return actions.all { authorizer.getAuthorization(it, this).isAllowed() }
    }

    suspend infix fun MediqToken.can(action: Action): Boolean {
        return authorizer.getAuthorization(action, this).isAllowed()
    }

    suspend infix fun MediqToken.can(action: Pair<Crud, Tables>): Boolean {
        return authorizer.getAuthorization(Action(action), this).isAllowed()
    }

    suspend infix fun MediqToken.can(actions: List<Pair<Crud, Tables>>): Boolean {
        return actions.all { authorizer.getAuthorization(Action(it), this).isAllowed() }
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