package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoesNotExist
import com.leftindust.mockingbird.dao.NotAuthorized
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GraphQLUserTest {

    @Test
    fun `isRegistered general success`() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val userDao = mockk<UserDao>() {
            coEvery { getUserByUid("uid", any()) } returns Success(mockk())
        }

        val graphQLUser = runBlocking { GraphQLUser("uid", null, authContext).isRegistered(userDao) }
        assertEquals(true, graphQLUser)
    }

    @Test
    fun `isRegistered special success`() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val userDao = mockk<UserDao>() {
            coEvery { getUserByUid("uid", any()) } returns Failure(DoesNotExist())
        }

        val graphQLUser = runBlocking { GraphQLUser("uid", null, authContext).isRegistered(userDao) }
        assertEquals(false, graphQLUser)
    }

    @Test
    fun `isRegistered general failure`() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val userDao = mockk<UserDao>() {
            coEvery { getUserByUid("uid", any()) } returns Failure(
                NotAuthorized(
                    authContext.mediqAuthToken,
                    "cannot do that!"
                )
            )
        }

        assertThrows<Exception> {
            runBlocking { GraphQLUser("uid", null, authContext).isRegistered(userDao) }
        }
    }

    @Test
    @Disabled
    fun firebaseUserInfo() {
        TODO()
    }

    @Test
    @Disabled
    fun permissions() {
        TODO()
    }

    @Test
    @Disabled
    fun hasPermission() {
        TODO()
    }
}