package com.leftindust.mockingbird.auth.impl

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.leftindust.mockingbird.auth.MediqToken
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * firebase implementation for Mediq token
 * @param token the string token to be checked against the firebase service
 * @returns a MediqToken instance
 */
class MediqFireBaseToken constructor(token: String?) : MediqToken {
    val logger: Logger = LogManager.getLogger()

    private val firebase: FirebaseToken? = try {
        FirebaseAuth.getInstance().verifyIdToken(token)
    } catch (cause: Exception) {
        logger.error(GraphQLKotlinException("error verifying Id Token", cause))
        null
    }

    override val uid: String? = firebase?.uid

    override fun isVerified(): Boolean {
        return this.firebase != null && this.uid != null
    }

    override fun toString(): String {
        return "MediqFireBaseToken(uid=$uid)"
    }
}