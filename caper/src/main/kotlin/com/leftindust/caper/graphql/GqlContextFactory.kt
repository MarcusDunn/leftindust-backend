package com.leftindust.caper.graphql

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.google.firebase.auth.FirebaseAuth
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

/**
 * Handles turning headers into GraphQLContext
 */
@Component
class GqlContextFactory : GraphQLContextFactory<GqlAuthContext, ServerHttpRequest> {

    override suspend fun generateContext(request: ServerHttpRequest): GqlAuthContext? {
        if (request.method != HttpMethod.OPTIONS) {
            val authToken = request.headers["mediq-auth-token"]?.first()
            if (authToken != null) {
                return GqlAuthContext(MediqFireBaseToken(authToken))
            }
        }
        return null
    }

    abstract class MediqToken {
        abstract val uid: String
    }

    class MediqFireBaseToken(token: String) : MediqToken() {
        companion object {
            val firebaseInstance: FirebaseAuth = FirebaseAuth.getInstance()
        }

        private val firebaseToken = firebaseInstance.verifyIdToken(token)

        override val uid: String = firebaseToken.uid

    }
}

data class GqlAuthContext(val mediqAuthToken: GqlContextFactory.MediqToken) : GraphQLContext

