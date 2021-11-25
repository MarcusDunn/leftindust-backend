package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.impl.repository.HibernateAssignedFormRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLFormTemplateInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import java.util.*

internal class FormDaoImplTest {
    private val formRepository = mockk<HibernateFormRepository>()
    private val assignedFormRepository = mockk<HibernateAssignedFormRepository>()

    private val authorizer = mockk<Authorizer>()

    private val uuid = UUID.nameUUIDFromBytes("seat".toByteArray())


    @Test
    fun getByIds() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val expected = mockk<Form>()
        every { formRepository.findAllById(any()) } returns listOf(expected)

        val formDao = FormDaoImpl(formRepository, assignedFormRepository, authorizer)
        val result = runBlocking {
            formDao.getByIds(listOf(GraphQLFormTemplate.ID(uuid)), mockk())
        }

        assertEquals(setOf(expected), result.toSet())
    }

    @Test
    fun getByGetMany() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val expected = mockk<Form>()
        every { formRepository.findAll(any<Pageable>()) } returns mockk {
            every { toList() } returns listOf(expected)
        }

        val formDao = FormDaoImpl(formRepository, assignedFormRepository, authorizer)
        val result = runBlocking {
            formDao.getMany(GraphQLRangeInput(0, 2), mockk())
        }

        assertEquals(setOf(expected), result.toSet())
    }

    @Test
    fun addForm() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val expected = mockk<Form>()

        val mockkGqlForm = mockk<GraphQLFormTemplateInput>(relaxed = true)

        every { formRepository.save(any()) } returns expected

        val formDao = FormDaoImpl(formRepository, assignedFormRepository, authorizer)

        val result = runBlocking { formDao.addForm(mockkGqlForm, mockk()) }

        assertEquals(expected, result)
    }


    @Test
    fun deleteForm() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val expected = mockk<Form>()
        every { formRepository.getById(any()) } returns expected
        every { formRepository.delete(any()) } just runs

        val formDao = FormDaoImpl(formRepository, assignedFormRepository, authorizer)

        val result = runBlocking { formDao.deleteForm(mockk { every { id } returns mockk() }, mockk()) }

        assertEquals(expected, result)
    }
}