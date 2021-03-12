package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.entity.UserSettings
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@Transactional
@SpringBootTest
internal class AuthorizationDaoTest(
    @Autowired private val authorizationDao: AuthorizationDao,
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getRolesForUserByUid() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        )
        val action = Action(Crud.READ to Tables.Patient)
        session.save(user)
        session.save(action)
        val acl = AccessControlList(
            group = null,
            mediqUser = user,
            action = action
        )
        session.save(acl)

        val result = runBlocking { authorizationDao.getRolesForUserByUid(user.uniqueId) }

        assertEquals(result.unwrap(), listOf(acl))

    }

    @Test
    fun `getRolesForUserByUid with no roles`() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        )
        session.save(user)

        val result = runBlocking { authorizationDao.getRolesForUserByUid(user.uniqueId) }

        assertEquals(result.unwrap(), emptyList<AccessControlList>())

    }

    // Getting coffee

    @Test
    suspend fun `getRolesForUserByUid with non-existent user`() {
        val result = authorizationDao.getRolesForUserByUid("TEST INVALID UNIQUE ID")
        assert(result.unwrapFailure() is DoesNotExist)
    }
}