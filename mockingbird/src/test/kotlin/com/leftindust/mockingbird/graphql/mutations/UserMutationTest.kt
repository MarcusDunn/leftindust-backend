package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.sun.net.httpserver.Authenticator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserMutationTest {
    private val userDao = mockk<UserDao>()
    private val authContext = mockk<GraphQLAuthContext>()


    @Test
    fun addUser() {
        val mockkUser = mockk<MediqUser>(relaxed = true)

        every { authContext.mediqAuthToken } returns mockk()

        val mockkGraphQLUser = GraphQLUser(mockkUser, authContext)

        coEvery { userDao.addUser(any(), any()) } returns Success(mockkUser)

        val userMutation = UserMutation(userDao)

        val result = runBlocking { userMutation.addUser(mockk(), authContext) }

        assertEquals(mockkGraphQLUser, result)
    }
}