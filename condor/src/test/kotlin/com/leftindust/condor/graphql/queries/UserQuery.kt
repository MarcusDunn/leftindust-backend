package com.leftindust.condor.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.google.firebase.auth.FirebaseAuth
import com.leftindust.condor.graphql.types.GraphQLUser
import org.springframework.stereotype.Component

@Component
class UserQuery(
    private val firebase: FirebaseAuth
) : Query {

    suspend fun users(uids: List<ID>? = null): List<GraphQLUser> {
        return when {
            uids != null ->
                uids
                    .map { firebase.getUser(it.value)!! } // we throw here so input list length always matches output list length
                    .map { GraphQLUser(it) }
            else -> throw IllegalArgumentException("invalid argument combination to users")
        }
    }
}