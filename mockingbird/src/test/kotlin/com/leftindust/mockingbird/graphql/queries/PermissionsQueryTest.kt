package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.AuthorizationDao
import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.graphql.types.GraphQLPermission
import com.leftindust.mockingbird.graphql.types.GraphQLPermissions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PermissionsQueryTest {
    private val authorizer = mockk<AuthorizationDao>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun permissions() {
        val acl = mockk<AccessControlList>(relaxed = true)
        every { authContext.mediqAuthToken } returns mockk {
            every { isVerified() } returns true
        }
        coEvery { authorizer.getRolesForUserByUid("uid") } returns mockk() {
            every { getOrThrow() } returns listOf(acl)
        }
        val permissionsQuery = PermissionsQuery(authorizer)
        val result = runBlocking { permissionsQuery.permissions("uid", authContext) }
        val permission = GraphQLPermissions(listOf(acl))
        assertEquals(permission, result)
    }
}