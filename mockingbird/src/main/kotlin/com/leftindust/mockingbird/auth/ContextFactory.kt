package com.leftindust.mockingbird.auth


import com.expediagroup.graphql.execution.GraphQLContext
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import com.leftindust.mockingbird.auth.impl.MediqFireBaseToken
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component

/**
 * Handles turning headers into GraphQLContext
 */
@Component
class ContextFactory : GraphQLContextFactory<GraphQLAuthContext> {
    val logger: Logger = LogManager.getLogger()

    override suspend fun generateContext(request: ServerHttpRequest, response: ServerHttpResponse): GraphQLAuthContext {
        return if (request.method != HttpMethod.OPTIONS) {
            GraphQLAuthContext(MediqFireBaseToken(request.headers["mediq-auth-token"]?.first()))
        } else {
            GraphQLAuthContext(MediqFireBaseToken(null))
        }
    }
}

/**
 * the mediq specific data returned from the ContextFactory
 * @property mediqAuthToken the authentication token
 */
data class GraphQLAuthContext(val mediqAuthToken: MediqToken) : GraphQLContext