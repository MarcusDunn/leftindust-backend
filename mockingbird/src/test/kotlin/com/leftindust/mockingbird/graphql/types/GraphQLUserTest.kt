package com.leftindust.mockingbird.graphql.types

import com.google.firebase.auth.UserRecord
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.external.firebase.UserFetcher
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun firebaseUserInfo() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val expected = mockk<UserRecord>(relaxed = true)

        val userFetcher = mockk<UserFetcher>() {
            coEvery { getUserInfo("uid", authContext.mediqAuthToken) } returns expected
        }
        val result = runBlocking { GraphQLUser("uid", null, authContext).firebaseUserInfo(userFetcher) }

        assertEquals(GraphQLFirebaseInfo(expected), result)
    }

    @Test
    fun permissions() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val authorizationDao = mockk<AuthorizationDao>() {
            coEvery { getRolesForUserByUid("uid") } returns emptyList()
        }

        val result = runBlocking { GraphQLUser("uid", null, authContext).permissions(authorizationDao) }

        assertEquals(GraphQLPermissions(emptyList()), result)
    }

    @Test
    fun hasPermission() {
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val authorizationDao = mockk<AuthorizationDao>() {
            coEvery { getRolesForUserByUid("uid") } returns listOf(mockk() {
                every { action } returns Action(Crud.UPDATE to Tables.Patient)
            })
        }

        val result = runBlocking {
            GraphQLUser("uid", null, authContext).hasPermission(
                authorizationDao,
                GraphQLPermissionInput(referencedTableName = Tables.Patient, permissionType = Crud.UPDATE)
            )
        }

        assertEquals(true, result)
    }
}