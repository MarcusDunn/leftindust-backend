package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.google.firebase.auth.ExportedUserRecord
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.NotAuthorized
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.external.firebase.UserFetcher
import com.leftindust.mockingbird.graphql.types.GraphQLFirebaseInfo
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import org.springframework.stereotype.Component

@Component
class UserQuery(
    private val userDao: UserDao,
    private val firebaseFetcher: UserFetcher
) : Query {
    suspend fun user(uid: ID, graphQLAuthContext: GraphQLAuthContext): GraphQLUser {
        val strUid = uid.value
        return try {
            val user = userDao.getUserByUid(strUid, graphQLAuthContext.mediqAuthToken)
                .getOrThrow() // check if user exists in database
            GraphQLUser(user, graphQLAuthContext)
        } catch (e: CustomResultException) {
            if (graphQLAuthContext.mediqAuthToken.isVerified()) {
                when (val result = firebaseFetcher.getUserInfo(strUid, graphQLAuthContext.mediqAuthToken)) {
                    is Failure -> result.getOrThrow()
                    is Success -> GraphQLUser(
                        uid = result.value.uid!!,
                        authContext = graphQLAuthContext
                    )
                }
            } else {
                Failure(NotAuthorized(graphQLAuthContext.mediqAuthToken)).getOrThrow()
            }
        }
    }

    @GraphQLDescription(
        """returns a list of users sorted by UID
from is inclusive and to is exclusive.
the arguments must match the following predicate
(range != null) -> (ids == null)
the default arguments are users(RangeInput(0,20))
"""
    )
    suspend fun users(
        range: GraphQLRangeInput? = null,
        uniqueIds: List<ID>? = null,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLUser> {
        return when {
            uniqueIds == null -> {
                val validatedRange = (range ?: GraphQLRangeInput(0, 20)).toIntRange()
                userDao
                    .getUsers(validatedRange.first, validatedRange.last, graphQLAuthContext.mediqAuthToken)
                    .getOrThrow()

            }
            range == null -> {
                uniqueIds.map { userDao.getUserByUid(it.value, graphQLAuthContext.mediqAuthToken).getOrThrow() }
            }
            else -> {
                throw GraphQLKotlinException(
                    "the arguments must match the following predicate: (range != null) -> (ids == null)",
                    IllegalArgumentException()
                )
            }
        }.map { GraphQLUser(it, graphQLAuthContext) }
    }

    @GraphQLDescription(
        """gets all users from firebase, you can
filter out already registered 
users by setting filterRegistered 
to true (defaults to false)"""
    )
    suspend fun firebaseUsers(
        range: GraphQLRangeInput? = null,
        filterRegistered: Boolean? = false,
        graphQLAuthContext: GraphQLAuthContext
    ): List<GraphQLFirebaseInfo> {
        val users = firebaseFetcher
            .getUsers(graphQLAuthContext.mediqAuthToken)
            .getOrThrow()
        val nnRange = range ?: GraphQLRangeInput(0, 20)
        val validatedRange = nnRange.toIntRange()

        val returnedUsers = emptyList<ExportedUserRecord>().toMutableList()
        users.takeWhile { returnedUsers.size < validatedRange.last }
            .filter { userDao.getUserByUid(it.uid, graphQLAuthContext.mediqAuthToken).isSuccess() == filterRegistered }
            .forEach { returnedUsers.add(it) }

        return returnedUsers
            .drop(validatedRange.first)
            .map { GraphQLFirebaseInfo(it) }
    }
}