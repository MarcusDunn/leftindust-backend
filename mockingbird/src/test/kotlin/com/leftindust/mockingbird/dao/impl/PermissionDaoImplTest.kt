package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud.CREATE
import com.leftindust.mockingbird.dao.Tables.User
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.entity.MediqUser
import com.leftindust.mockingbird.dao.impl.repository.HibernateAclRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateUserRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLUserGroup
import com.leftindust.mockingbird.graphql.types.input.GraphQLPermissionInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class PermissionDaoImplTest {
    private val authorizer = mockk<Authorizer> {
        coEvery { getAuthorization(any(), any()) } returns Authorization.Allowed
    }

    @Test
    fun `check addUserPermission success`(): Unit = runBlocking {
        val uuid = UUID.randomUUID()
        val mediqGroup = mockk<MediqGroup>()
        val groupRepository = mockk<HibernateGroupRepository>() {
            every { getById(uuid) } returns mediqGroup
        }
        val accessControlList = mockk<AccessControlList>()
        val aclRepository = mockk<HibernateAclRepository> {
            every { save(any()) } returns accessControlList
        }
        val mediqUser = mockk<MediqUser>()
        val userRepository = mockk<HibernateUserRepository> {
            every { getByUniqueId("uuid") } returns mediqUser
        }
        val permissionDao = PermissionDaoImpl(groupRepository, aclRepository, userRepository, authorizer)
        val result = permissionDao.addUserPermission("uuid", GraphQLPermissionInput(User, CREATE), mockk())
        assertEquals(accessControlList, result)
    }

    @Test
    fun `check addGroupPermission success`(): Unit = runBlocking {
        val mediqGroup = mockk<MediqGroup>()
        val groupRepository = mockk<HibernateGroupRepository> {
            every { getById(any()) } returns mediqGroup
        }
        val accessControlList = mockk<AccessControlList>()
        val aclRepository = mockk<HibernateAclRepository> {
            every { save(any()) } returns accessControlList
        }
        val uuid = UUID.randomUUID()
        val mockk = mockk<MediqUser>()
        val userRepository = mockk<HibernateUserRepository> {
            every { getById(uuid) } returns mockk
        }
        val permissionDao = PermissionDaoImpl(groupRepository, aclRepository, userRepository, authorizer)
        val result =
            permissionDao.addGroupPermission(GraphQLUserGroup.ID(uuid), GraphQLPermissionInput(User, CREATE), mockk())
        assertEquals(accessControlList, result)
    }
}