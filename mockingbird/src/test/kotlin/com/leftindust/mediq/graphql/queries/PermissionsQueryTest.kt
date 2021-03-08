package com.leftindust.mediq.graphql.queries

import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.leftindust.mediq.auth.Crud
import com.leftindust.mediq.dao.Tables
import com.leftindust.mediq.dao.entity.*
import com.leftindust.mediq.graphql.types.GraphQLPermission
import com.leftindust.mediq.graphql.types.GraphQLPermissions
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class PermissionsQueryTest(
    @Autowired private val permissionsQuery: PermissionsQuery
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun permissions() {
        val (user, acl1, acl2) = addUserGroupAndTwoAcls()

        val result = runBlocking { permissionsQuery.permissions(user.uniqueId, FakeAuth.Valid.Context) }

        val expected = GraphQLPermissions(
            groupPerms = listOf(GraphQLPermission(acl2.action)),
            userPerms = listOf(GraphQLPermission(acl1.action))
        )
        assertEquals(expected, result)
    }

    @Test
    fun `permissions with valid uid but invalid context`() {
        val (user) = addUserGroupAndTwoAcls()

        assertThrows<GraphQLKotlinException> {
            runBlocking { permissionsQuery.permissions(user.uniqueId, FakeAuth.Invalid.Context) }
        }
    }

    @Test
    fun `permissions with invalid uid but valid context`() {
        addUserGroupAndTwoAcls()

        assertThrows<GraphQLKotlinException> {
            runBlocking { permissionsQuery.permissions("WEE WOO WEE WOO", FakeAuth.Valid.Context) }
        }
    }

    private fun addUserGroupAndTwoAcls(): Triple<MediqUser, AccessControlList, AccessControlList> {
        val user = MediqUser(
            uniqueId = "TEST uniqueId",
            settings = UserSettings(1)
        )
        session.save(user)
        val group = MediqGroup(
            gid = 0,
            name = "TEST groupName"
        )
        user.group = group
        session.save(group)
        val acl1 = AccessControlList(
            group = null,
            mediqUser = user,
            action = Action(Crud.READ to Tables.Patient)
        )
        session.save(acl1)
        val acl2 = AccessControlList(
            group = group,
            mediqUser = null,
            action = Action(Crud.UPDATE to Tables.Patient)
        )
        session.save(acl2)
        return Triple(user, acl1, acl2)
    }
}