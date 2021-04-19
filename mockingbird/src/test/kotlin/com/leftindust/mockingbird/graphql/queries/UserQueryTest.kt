package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.google.firebase.auth.ExportedUserRecord
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.UserDao
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.extensions.Failure
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.external.firebase.UserFetcher
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserQueryTest {
    private val userDao = mockk<UserDao>()
    private val firebaseFetcher = mockk<UserFetcher>()
    private val graphQLAuthContext = mockk<GraphQLAuthContext>()

    @Test
    fun user() {
        val user = mockk<MediqUser> {
            every { uniqueId } returns "uid"
            every { group } returns null
        }
        coEvery { userDao.getUserByUid("uid", any()) } returns Success(user)
        val userQuery = UserQuery(userDao, firebaseFetcher)
        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val result = runBlocking { userQuery.user(ID("uid"), graphQLAuthContext) }
        assertEquals(GraphQLUser(user, graphQLAuthContext), result)
    }

    @Test
    fun users() {
        val user = mockk<MediqUser> {
            every { uniqueId } returns "uid"
            every { group } returns null
        }
        coEvery { userDao.getUsers(0, 3, any()) } returns listOf(user)
        val userQuery = UserQuery(userDao, firebaseFetcher)
        every { graphQLAuthContext.mediqAuthToken } returns mockk()
        val result = runBlocking { userQuery.users(GraphQLRangeInput(0, 3), graphQLAuthContext = graphQLAuthContext) }
        assertEquals(listOf(GraphQLUser(user, graphQLAuthContext)), result)
    }

    @Test
    fun `firebaseUsers when not all users are registered`() {
        val userRecords = (0 until 5).map {
            mockk<ExportedUserRecord>(relaxed = true) {
                every { uid } returns "uid$it"
            }
        }

        coEvery { userDao.getUserByUid("uid0", any()) } returns Success(mockk())
        coEvery { userDao.getUserByUid("uid1", any()) } returns Success(mockk())
        coEvery { userDao.getUserByUid("uid2", any()) } returns Success(mockk())
        coEvery { userDao.getUserByUid("uid3", any()) } returns Failure(mockk())
        coEvery { userDao.getUserByUid("uid4", any()) } returns Success(mockk())

        coEvery { firebaseFetcher.getUsers(any()) } returns Success(mockk {
            every { iterator() } returns mockk {
                every { hasNext() } returnsMany listOf(true, true, true, true, true, false)
                every { next() } returnsMany userRecords
            }
        }
        )

        val userQuery = UserQuery(userDao, firebaseFetcher)

        every { graphQLAuthContext.mediqAuthToken } returns mockk()

        val result = runBlocking { userQuery.firebaseUsers(GraphQLRangeInput(0, 4), true, graphQLAuthContext) }

        assertEquals(4, result.size)
    }
}