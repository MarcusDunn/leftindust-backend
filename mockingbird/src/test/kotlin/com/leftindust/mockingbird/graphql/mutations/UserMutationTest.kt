package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
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
    fun setUserSettings() {
        val mockkUser = mockk<MediqUser>(relaxed = true) {
            every { settings } returns mockk {
                every { version } returns 1
                every { settingsJSON } returns "{}"
            }
        }

        every { authContext.mediqAuthToken } returns mockk()

        val mockkSettings = mockk<GraphQLUser.Settings>() {
            every { settings } returns mockk {
                every { json } returns "{}"
                every { version } returns 2
            }
        }
        coEvery { userDao.setUserSettingsByUid(any(), any(), any(), any()) } returns mockk() {
            every { getOrThrow() } returns mockkUser
        }

        val mockkGraphQLUser = GraphQLUser(mockkUser, authContext)

        val userMutation = UserMutation(userDao)

        val result = runBlocking { userMutation.setUserSettings(mockk(), mockkSettings, authContext) }

        assertEquals(mockkGraphQLUser, result)
    }

    @Test
    fun addUser() {
        val mockkUser = mockk<MediqUser>(relaxed = true) {
            every { settings } returns mockk {
                every { version } returns 1
                every { settingsJSON } returns "{}"
            }
        }

        every { authContext.mediqAuthToken } returns mockk()

        val mockkGraphQLUser = GraphQLUser(mockkUser, authContext)

        coEvery { userDao.addUser(any<GraphQLUserInput>(), any()) } returns mockk {
            every { getOrThrow() } returns mockkUser
        }

        val userMutation = UserMutation(userDao)

        val result = runBlocking { userMutation.addUser(mockk(), authContext) }

        assertEquals(mockkGraphQLUser, result)
    }
}