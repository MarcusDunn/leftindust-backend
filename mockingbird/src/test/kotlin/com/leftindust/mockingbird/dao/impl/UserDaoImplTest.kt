package com.leftindust.mockingbird.dao.impl

import com.google.gson.JsonObject
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.entity.UserSettings
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Authorization
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val userRepository = mockk<HibernateUserRepository>()
    private val groupRepository = mockk<HibernateGroupRepository>()

    @Test
    fun getUserByUid() {
        val mockkUser = mockk<MediqUser>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { userRepository.getUserByUniqueId("test uid") } returns mockkUser

        val userDaoImpl = UserDaoImpl(authorizer, userRepository, groupRepository)

        val actual = runBlocking { userDaoImpl.getUserByUid("test uid", mockk()) }.getOrThrow()

        assertEquals(mockkUser, actual)
    }

    @Test
    fun setUserSettingsByUid() {
        val mockkUser = mockk<MediqUser> {
            every { id } returns 100
            every { settings } returns mockk()
        }
        every { mockkUser setProperty "settings" value any<UserSettings>() } just runs

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { userRepository.getUserByUniqueId("test uid") } returns mockkUser

        val userDaoImpl = UserDaoImpl(authorizer, userRepository, groupRepository)

        val actual = runBlocking { userDaoImpl.setUserSettingsByUid("test uid", 1, JsonObject(), mockk()) }.getOrThrow()

        assertEquals(mockkUser, actual)

        verify { mockkUser setProperty "settings" value any<UserSettings>() }
    }

    @Test
    fun addUser() {
        val mockkUser = mockk<MediqUser> {
            every { uniqueId } returns "uid"
        }
        every { userRepository.getUserByUniqueId("uid") } returns null // does not already exist in DB
        every { userRepository.save(mockkUser) } returns mockkUser

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val userDaoImpl = UserDaoImpl(authorizer, userRepository, groupRepository)

        val actual = runBlocking { userDaoImpl.addUser(mockkUser, mockk()) }.getOrThrow()

        assertEquals(mockkUser, actual)
    }
}