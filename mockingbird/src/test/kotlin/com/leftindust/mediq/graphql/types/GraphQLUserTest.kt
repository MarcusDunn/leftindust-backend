package com.leftindust.mediq.graphql.types

import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.dao.AuthorizationDao
import com.leftindust.mediq.dao.Tables
import com.leftindust.mediq.dao.entity.AccessControlList
import com.leftindust.mediq.dao.entity.Action
import com.leftindust.mediq.dao.entity.MediqUser
import com.leftindust.mediq.dao.entity.UserSettings
import com.leftindust.mediq.external.firebase.UserFetcher
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class GraphQLUserTest {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession


    @Test
    @Tag("firebase")
    fun firebaseUserInfo(@Autowired userFetcher: UserFetcher) {
        val user = createUser("c9LCrndzlCah2gFlX6evBEPI4zp2")
        val result = runBlocking { user.firebaseUserInfo(userFetcher) }

        Assertions.assertEquals("marcus.s.dunn@gmail.com", result.email)
    }

    @Test
    fun permissions(@Autowired authorizationDao: AuthorizationDao) {
        val user = createUser()
        val result = runBlocking { user.permissions(authorizationDao) }

        Assertions.assertEquals(emptyList<GraphQLPermissions>(), result.groupPerms + result.userPerms)

    }

    @Test
    fun `permissions with some permissions`(@Autowired authorizationDao: AuthorizationDao) {
        val userEntity = persistUser("c9LCrndzlCah2gFlX6evBEPI4zp2")
        val acl = AccessControlList(
            group = null,
            mediqUser = userEntity,
            action = Action(Crud.READ to Tables.Record)
        )
        val user = GraphQLUser(userEntity, FakeAuth.Valid.Context)
        session.save(acl)

        val result = runBlocking { user.permissions(authorizationDao) }

        Assertions.assertEquals(
                GraphQLPermission(
                    referencedTableName = Tables.Record,
                    permissionType = Crud.READ
                ),
            (result.groupPerms + result.userPerms).first()
        )
    }

    @Test
    fun hasPermission(@Autowired authorizationDao: AuthorizationDao) {
        val userEntity = persistUser("c9LCrndzlCah2gFlX6evBEPI4zp2")
        val acl = AccessControlList(
            group = null,
            mediqUser = userEntity,
            action = Action(Crud.READ to Tables.Record)
        )
        session.save(acl)
        val user = GraphQLUser(userEntity, FakeAuth.Valid.Context)

        val result = runBlocking {
            user.hasPermission(
                authorizationDao,
                GraphQLPermission(referencedTableName = Tables.Record, permissionType = Crud.READ)
            )
        }
        Assertions.assertEquals(true, result)

    }

    private fun createUser(uid: String? = null): GraphQLUser {
        return GraphQLUser(
            uid = uid ?: "fake uid",
            group = null,
            settings = null,
            authContext = FakeAuth.Valid.Context
        )
    }

    private fun persistUser(uid: String? = null): MediqUser {
        return MediqUser(
            uniqueId = uid ?: "fake uid",
            group = null,
            settings = UserSettings(
                version = 1,
                settingsJSON = "{}"
            )
        ).also { session.save(it) }
    }
}