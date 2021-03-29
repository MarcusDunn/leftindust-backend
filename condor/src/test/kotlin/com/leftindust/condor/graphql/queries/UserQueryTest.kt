package com.leftindust.condor.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.leftindust.condor.graphql.types.GraphQLUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserQueryTest {
    private val firebaseAuth = mockk<FirebaseAuth>()

    @Test
    fun users() {
        val mockkUser = mockk<UserRecord>() {
            every { uid } returns "1"
        }
        every { firebaseAuth.getUser("1") } returns mockkUser

        val userQuery = UserQuery(firebaseAuth)

        val result = runBlocking { userQuery.users(listOf(ID("1"))) }

        assertEquals(listOf(GraphQLUser(mockkUser)), result)
    }
}