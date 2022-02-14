package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.NameInfoDao
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Authorization
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.persistence.EntityNotFoundException

internal class NameInfoDaoImplTest {

    @Nested
    inner class Authorized {
        private val authorizer = mockk<Authorizer> {
            coEvery { getAuthorization(any(), any()) } returns Authorization.Allowed
        }

        @Nested
        inner class SuccessfullyFindUserByUniqueId {
            private val mediqUser = mockk<MediqUser>(relaxed = true)
            private val hibernateUserRepository = mockk<HibernateUserRepository> {
                every { findByUniqueId("id") } returns mediqUser
            }

            @Test
            fun `check getByUniqueId success`() {
                val nameInfoDao: NameInfoDao = NameInfoDaoImpl(hibernateUserRepository, authorizer)
                val result = runBlocking { nameInfoDao.getByUniqueId("id", mockk()) }
                assertEquals(mediqUser.nameInfo, result)
            }

            @Test
            fun `check findByUniqueId success`() {
                val nameInfoDao = NameInfoDaoImpl(hibernateUserRepository, authorizer)
                val result = runBlocking { nameInfoDao.findByUniqueId("id", mockk()) }
                assertEquals(mediqUser.nameInfo, result)
            }
        }

        @Nested
        inner class FailFindUserByUniqueId {
            private val hibernateUserRepository = mockk<HibernateUserRepository> {
                every { findByUniqueId("id") } returns null
            }
            private val nameInfoDao: NameInfoDao = NameInfoDaoImpl(hibernateUserRepository, authorizer)

            @Test
            fun `check getByUniqueId failure`(): Unit = runBlocking {
                assertThrows<EntityNotFoundException> {
                    nameInfoDao.getByUniqueId("id", mockk())
                }
            }

            @Test
            fun `check findByUniqueId success`(): Unit = runBlocking {
                val result = nameInfoDao.findByUniqueId("id", mockk())
                assertEquals(null, result)
            }
        }
    }

    @Nested
    inner class Unauthorized {
        private val authorizer = mockk<Authorizer> {
            coEvery { getAuthorization(any(), any()) } returns Authorization.Denied
        }

        @Nested
        inner class SuccessfullyFindUserByUniqueId {
            private val mediqUser = mockk<MediqUser>()
            private val hibernateUserRepository = mockk<HibernateUserRepository> {
                every { findByUniqueId("id") } returns mediqUser
            }
            private val nameInfoDao: NameInfoDao = NameInfoDaoImpl(hibernateUserRepository, authorizer)

            @Test
            fun `check getByUniqueId unauthorized`(): Unit = runBlocking {
                assertThrows<NotAuthorizedException> {
                    nameInfoDao.getByUniqueId("id", mockk())
                }
            }

            @Test
            fun `check findByUniqueId unauthorized`(): Unit = runBlocking {
                assertThrows<NotAuthorizedException> {
                    nameInfoDao.findByUniqueId("id", mockk())
                }
            }
        }

        @Nested
        inner class FailFindUserByUniqueId {
            private val hibernateUserRepository = mockk<HibernateUserRepository> {
                every { findByUniqueId("id") } returns null
            }
            private val nameInfoDao: NameInfoDao = NameInfoDaoImpl(hibernateUserRepository, authorizer)

            @Test
            fun `check getByUniqueId failure`(): Unit = runBlocking {
                assertThrows<NotAuthorizedException> {
                    nameInfoDao.getByUniqueId("id", mockk())
                }
            }

            @Test
            fun `check findByUniqueId failure`(): Unit = runBlocking {
                assertThrows<NotAuthorizedException> {
                    nameInfoDao.findByUniqueId("id", mockk())
                }
            }
        }
    }
}