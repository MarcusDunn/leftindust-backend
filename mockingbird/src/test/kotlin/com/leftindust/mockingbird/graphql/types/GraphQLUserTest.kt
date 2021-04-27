package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.UserDao
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
            coEvery { findUserByUid("uid", any()) } returns mockk()
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
            coEvery { findUserByUid("uid", any()) } returns null
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
            coEvery { findUserByUid("uid", any()) } throws NotAuthorizedException(
                authContext.mediqAuthToken,
                Crud.READ to Tables.User
            )
        }

        assertThrows<NotAuthorizedException> {
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