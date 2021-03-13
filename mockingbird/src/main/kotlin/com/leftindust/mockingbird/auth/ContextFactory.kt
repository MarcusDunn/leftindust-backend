package com.leftindust.mockingbird.auth


import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.google.firebase.auth.FirebaseAuth
import com.leftindust.mockingbird.auth.impl.VerifiedFirebaseToken
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

/**
 * Handles turning headers into GraphQLContext
 */
@Component
class ContextFactory : GraphQLContextFactory<GraphQLAuthContext> {
    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): GraphQLAuthContext {
        return if (request.method != HttpMethod.OPTIONS) {
            val token = request.headers["mediq-auth-token"]?.first()
            GraphQLAuthContext(VerifiedFirebaseToken(token))
        } else {
            GraphQLAuthContext(VerifiedFirebaseToken(null))
        }
    }
}

/**
 * the mediq specific data returned from the ContextFactory
 * @property mediqAuthToken the authentication token
 */
data class GraphQLAuthContext(val mediqAuthToken: MediqToken) : GraphQLContext