package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import com.leftindust.mockingbird.extensions.getByIds
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import java.util.*

internal class FormDaoImplTest {
    private val formRepository = mockk<HibernateFormRepository>()
    private val authorizer = mockk<Authorizer>()

    private val uuid = UUID.nameUUIDFromBytes("seat".toByteArray())


    @Test
    fun getByIds() {
        val expected = mockk<Form>()
        every { formRepository.findAllById(any()) } returns listOf(expected)

        val formDao = FormDaoImpl(formRepository, authorizer)
        val result = runBlocking {
            formDao.getByIds(listOf(GraphQLFormTemplate.ID(uuid)), mockk())
        }

        assertEquals(setOf(expected), result.toSet())
    }

    @Test
    fun getByGetMany() {
        val expected = mockk<Form>()
        every { formRepository.findAll(any<Pageable>()) } returns mockk {
            every { toList() } returns listOf(expected)
        }

        val formDao = FormDaoImpl(formRepository, authorizer)
        val result = runBlocking {
            formDao.getMany(GraphQLRangeInput(0, 2), mockk())
        }

        assertEquals(setOf(expected), result.toSet())
    }
}