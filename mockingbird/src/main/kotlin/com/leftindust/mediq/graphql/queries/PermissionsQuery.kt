package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.server.operations.Query
import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.dao.AuthorizationDao
import com.leftindust.mediq.graphql.types.GraphQLPermissions
import org.springframework.stereotype.Component

@Component
class PermissionsQuery(
    private val authorizer: AuthorizationDao
) : Query {
    suspend fun permissions(uid: String, graphQLAuthContext: GraphQLAuthContext): GraphQLPermissions {
        if (graphQLAuthContext.mediqAuthToken.isVerified()) {
            return authorizer
                .getRolesForUserByUid(uid)
                .getOrThrow()
                .let { GraphQLPermissions(it) }
        } else {
            throw GraphQLKotlinException("unauthenticated token")
        }

    }
}