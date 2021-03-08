package com.leftindust.mediq.graphql.mutations

import com.expediagroup.graphql.scalars.ID
import com.google.gson.JsonObject
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.dao.entity.UserSettings
import com.leftindust.mediq.extensions.CustomResultException
import com.leftindust.mediq.graphql.types.GraphQLJsonObject
import com.leftindust.mediq.graphql.types.GraphQLUser
import com.leftindust.mediq.graphql.types.input.GraphQLUserInput
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class UserMutationTest(
    @Autowired private val userMutation: UserMutation
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession


    @Test
    fun setUserSettings() {
        val user = MediqUser(
            uniqueId = "TEST uniqueId",
            settings = UserSettings(1)
        )
        session.save(user)
        val newSettings = JsonObject().apply {
            addProperty("name", "settings")
        }

        val result =
            runBlocking {
                userMutation.setUserSettings(
                    ID(user.uniqueId),
                    GraphQLUser.Settings(1, GraphQLJsonObject(newSettings)),
                    FakeAuth.Valid.Context
                )
            }

        assertEquals(newSettings.toString(), result.settings!!.settings.json)
    }


    @Test
    fun `setUserSettings persists`() {
        val user = MediqUser(
            uniqueId = "TEST uniqueId",
            settings = UserSettings(1)
        )
        val id = session.save(user)
        val newSettings = JsonObject().apply {
            addProperty("name", "settings")
        }.toString()
        runBlocking {
            userMutation.setUserSettings(
                ID(user.uniqueId),
                GraphQLUser.Settings(1, GraphQLJsonObject(newSettings)),
                FakeAuth.Valid.Context
            )
        }

        val newUser = session.get(MediqUser::class.java, id)

        assertEquals(newSettings, newUser.settings.settingsJSON)
    }

    @Test
    fun `setUserSettings with no such user`() {
        val exception = assertThrows(CustomResultException::class.java) {
            val settingsJson = GraphQLJsonObject(JsonObject().toString())
            val newSettings = GraphQLUser.Settings(1, settingsJson)
            runBlocking { userMutation.setUserSettings(ID("DOES NOT EXIST"), newSettings, FakeAuth.Valid.Context) }
        }

        assert(exception.message!!.contains("DoesNotExist"))
    }

    @Test
    fun addUser() {
        val user = GraphQLUserInput(
            uid = "TEST NEW USER",
            group_id = null,
            settings_version = 1,
            settings = GraphQLJsonObject(
                json = JsonObject().toString()
            ),
        )

        val expected = GraphQLUser(
            uid = user.uid,
            group = null,
            settings = GraphQLUser.Settings(
                version = 1,
                settings = GraphQLJsonObject(
                    json = JsonObject().toString()
                )
            ),
            authContext = FakeAuth.Valid.Context,
        )
        val result = runBlocking { userMutation.addUser(user, FakeAuth.Valid.Context) }

        assertEquals(expected, result)
    }

    @Test
    fun `addUser with already existing user`() {
        val user = MediqUser(
            uniqueId = "TEST NEW USER",
            settings = UserSettings(1)
        )
        session.save(user)
        val gqlUser = GraphQLUserInput(
            uid = "TEST NEW USER",
            group_id = null,
            settings_version = 1,
            settings = GraphQLJsonObject(
                json = JsonObject().toString()
            ),
        )

        val exception = assertThrows(CustomResultException::class.java) {
            runBlocking { userMutation.addUser(gqlUser, FakeAuth.Valid.Context) }
        }

        assert(exception.message!!.contains("AlreadyExists"))
    }
}