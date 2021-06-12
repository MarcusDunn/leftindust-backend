package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.input.GraphQLUserInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UserDaoImplTest {
    private val authorizer = mockk<Authorizer>()
    private val userRepository = mockk<HibernateUserRepository>()
    private val groupRepository = mockk<HibernateGroupRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()

    @Test
    fun getUserByUid() {
        val mockkUser = mockk<MediqUser>()
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { userRepository.getByUniqueId("test uid") } returns mockkUser

        val userDaoImpl = UserDaoImpl(authorizer, userRepository, groupRepository, doctorRepository)

        val actual = runBlocking { userDaoImpl.getUserByUid("test uid", mockk()) }

        assertEquals(mockkUser, actual)
    }

    @Test
    fun addUser() {
        val mockkUser = mockk<GraphQLUserInput>(relaxed = true) {
            every { uid } returns "uid"
            every { group } returns null
            every { doctor } returns null
        }
        val mockkMediqUser = mockk<MediqUser>()
        every { userRepository.save(any()) } returns mockkMediqUser

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val userDaoImpl = UserDaoImpl(authorizer, userRepository, groupRepository, doctorRepository)

        val actual = runBlocking { userDaoImpl.addUser(mockkUser, mockk()) }

        assertEquals(mockkMediqUser, actual)
    }
}