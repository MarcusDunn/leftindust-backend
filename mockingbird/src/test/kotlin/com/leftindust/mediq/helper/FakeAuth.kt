package com.leftindust.mediq.helper

import com.leftindust.mediq.auth.GraphQLAuthContext
import com.leftindust.mediq.auth.MediqToken

object FakeAuth {
    object Valid {
        val Token = object : MediqToken {
            override val uid: String = "admin"
            override fun isVerified() = true
        }
        val Context = GraphQLAuthContext(
            mediqAuthToken = this.Token
        )
    }

    object Invalid {
        val Token = object : MediqToken {
            override val uid: Nothing? = null
            override fun isVerified() = false
        }
        val Context = GraphQLAuthContext(
            mediqAuthToken = this.Token
        )
    }
}