package com.leftindust.mockingbird.external.firebase

import com.google.firebase.auth.ExportedUserRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.extensions.Authorization
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserFetcherTest {
    private val authorizer = mockk<Authorizer>()
    private val firebaseAuth = mockk<FirebaseAuth>()

    @Test
    fun getUserInfo() {
        val mockkUser = mockk<UserRecord>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { firebaseAuth.getUser("uid") } returns mockkUser

        val userFetcher = UserFetcher(authorizer, firebaseAuth)

        val result = runBlocking { userFetcher.getUserInfo("uid", mockk()) }.getOrThrow()

        assertEquals(mockkUser, result)
    }

    @Test
    fun getUsers() {
        val mockkIterable = mockk<MutableIterable<ExportedUserRecord>>()

        every { firebaseAuth.listUsers(null) } returns mockk {
            every { iterateAll() } returns mockkIterable
        }

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val userFetcher = UserFetcher(authorizer, firebaseAuth)

        val result = runBlocking { userFetcher.getUsers(mockk()) }.getOrThrow()

        assertEquals(mockkIterable, result)
    }
}