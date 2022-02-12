package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.impl.repository.HibernateGroupRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLUser
import com.leftindust.mockingbird.graphql.types.input.GraphQLGroupInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import java.util.*

internal class GroupDaoImplTest {
    private val lenientAuthorizer = mockk<Authorizer> {
        coEvery { getAuthorization(any(), any()) } returns Authorization.Allowed
    }

    @Test
    fun `check addGroup success`(): Unit = runBlocking {
        val mediqGroup = mockk<MediqGroup>()
        val groupRepository = mockk<HibernateGroupRepository> {
            every { save(any()) } returns mediqGroup
        }
        val groupDaoImpl = GroupDaoImpl(lenientAuthorizer, groupRepository)
        val result = groupDaoImpl.addGroup(GraphQLGroupInput("doctors"), mockk())
        assertEquals(mediqGroup, result)
    }

    @Test
    fun `check getGroupById success`(): Unit = runBlocking {
        val mediqGroup = mockk<MediqGroup>()
        val groupRepository = mockk<HibernateGroupRepository> {
            every { getById(any()) } returns mediqGroup
        }
        val groupDaoImpl = GroupDaoImpl(lenientAuthorizer, groupRepository)
        val result = groupDaoImpl.getGroupById(GraphQLUser.Group.ID(UUID.randomUUID()), mockk())
        assertEquals(mediqGroup, result)
    }

    @Test
    fun `check getRange success`(): Unit = runBlocking {
        val mediqGroup = mockk<MediqGroup>()
        val groupRepository = mockk<HibernateGroupRepository> {
            every { findAll(any<Pageable>()) } returns mockk {
                every { content } returns listOf(mediqGroup)
            }
        }
        val groupDaoImpl = GroupDaoImpl(lenientAuthorizer, groupRepository)
        val result = groupDaoImpl.getRange(GraphQLRangeInput(0, 10), mockk())
        assertEquals(listOf(mediqGroup), result)
    }
}