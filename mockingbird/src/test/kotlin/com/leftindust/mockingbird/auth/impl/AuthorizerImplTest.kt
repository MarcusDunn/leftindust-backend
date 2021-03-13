package com.leftindust.mockingbird.auth.impl

import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.extensions.Success
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class AuthorizerImplTest {

    @MockK
    private lateinit var authorizationDao: AuthorizationDao

    @Test
    fun `get authorization when user has subset of required permissions`() {
        coEvery { authorizationDao.getRolesForUserByUid("marcus") } returns Success(listOf(
            mockk {
                every { action.isSuperset(any()) } returns false
            }
        ))

        val authorizer = AuthorizerImpl(authorizationDao)

        val actual = runBlocking {
            authorizer.getAuthorization(
                action = mockk(),
                user = mockk("marcus user") { every { uid } returns "marcus" })
        }

        assertEquals(Authorization.Denied, actual)

        coVerify { authorizationDao.getRolesForUserByUid("marcus") }
    }

    @Test
    fun `get authorization when user has superset of required permissions`() {

        coEvery { authorizationDao.getRolesForUserByUid("marcus") } returns Success(listOf(
            mockk("superset") {
                every { action.isSuperset(any()) } returns true
            }
        ))

        val authorizer = AuthorizerImpl(authorizationDao)

        val actual = runBlocking {
            authorizer.getAuthorization(
                action = mockk("subset"),
                user = mockk("marcus user") { every { uid } returns "marcus" })
        }

        assertEquals(Authorization.Allowed, actual)

        coVerify { authorizationDao.getRolesForUserByUid("marcus") }
    }
}