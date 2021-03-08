package com.leftindust.mediq.dao

import com.google.gson.JsonObject
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.dao.entity.UserSettings
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@SpringBootTest
@Transactional
internal class UserDaoTest(
    @Autowired private val userDao: UserDao
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getUserByUid() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        )
        session.save(user)

        val result = runBlocking { userDao.getUserByUid(user.uniqueId, FakeAuth.Valid.Token) }

        assertEquals(user, result.unwrap())
    }

    @Test
    fun `getUserByUid with no such user`() {
        val result = runBlocking { userDao.getUserByUid("DOES NOT EXIST", FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist) { result }
    }

    @Test
    fun setUserSettingsByUid() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        )
        session.save(user)
        val newSettings = JsonObject().apply {
            addProperty("setting1", true)
        }

        val result = runBlocking {
            userDao.setUserSettingsByUid(user.uniqueId, user.settings.version, newSettings, FakeAuth.Valid.Token)
        }

        assertEquals(result.unwrap().settings.settingsJSON, newSettings.toString())
    }


    @Test
    fun `setUserSettingsByUid changes persist`() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        )
        val id = session.save(user)
        val newSettings = JsonObject().apply {
            addProperty("setting1", true)
        }
        runBlocking {
            userDao.setUserSettingsByUid(user.uniqueId, user.settings.version, newSettings, FakeAuth.Valid.Token)
        }

        val result = session.get(MediqUser::class.java, id)

        assertEquals(result.settings.settingsJSON, newSettings.toString())
    }

    @Test
    fun `setUserSettingsByUid with no such user`() {
        val newSettings = JsonObject().apply {
            addProperty("setting1", true)
        }

        val result = runBlocking { userDao.setUserSettingsByUid("DOES NOT EXIST", 1, newSettings, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist) { result }
    }

    @Test
    fun addUser() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = JsonObject().toString()
            )
        )

        val result = runBlocking { userDao.addUser(user, FakeAuth.Valid.Token) }

        assertEquals(user, result.unwrap())
    }

    @Test
    fun `addUser persists`() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = JsonObject().toString()
            )
        )
        val addedUser = runBlocking { userDao.addUser(user, FakeAuth.Valid.Token) }

        val result = session.get(MediqUser::class.java, addedUser.unwrap().id)

        assertEquals(user, result)
    }

    @Test
    fun `addUser with already existing user`() {
        val user = MediqUser(
            uniqueId = "TEST USER",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = JsonObject().toString()
            )
        )
        session.save(user)

        val result = runBlocking { userDao.addUser(user, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is AlreadyExists) { result }
    }
}