package com.leftindust.mockingbird.graphql.queries

import com.expediagroup.graphql.generator.exceptions.GraphQLKotlinException
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class FormTemplateQueryTest {
    private val formDao = mockk<FormDao>()

    @Test
    fun `forms by id`() {
        val uuid  = GraphQLFormTemplate.ID(UUID.nameUUIDFromBytes("seat".toByteArray()))
        val form = mockk<Form>(relaxed = true)
        coEvery { formDao.getById(uuid, any()) } returns form

        val formTemplateQuery = FormTemplateQuery(formDao)
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result = runBlocking {
            formTemplateQuery.forms(
                form = uuid,
                authContext = authContext
            )
        }
        assertEquals(listOf(GraphQLFormTemplate(form, authContext)), result)
    }

    @Test
    fun `forms by doctor`() {
        val uuid  = GraphQLDoctor.ID(UUID.nameUUIDFromBytes("seat".toByteArray()))
        val form = mockk<Form>(relaxed = true)
        coEvery { formDao.getByDoctorId(uuid, any()) } returns listOf(form)
        val formTemplateQuery = FormTemplateQuery(formDao)
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val result = runBlocking {
            formTemplateQuery.forms(
                doctor = GraphQLDoctor.ID(UUID.nameUUIDFromBytes("seat".toByteArray())),
                authContext = authContext
            )
        }
        assertEquals(listOf(GraphQLFormTemplate(form, authContext)), result)
    }

    @Test
    fun `invalid forms argument`() {
        val formTemplateQuery = FormTemplateQuery(formDao)
        assertThrows<GraphQLKotlinException> {
            runBlocking {
                formTemplateQuery.forms(null, null, mockk())
            }
        }
        assertThrows<GraphQLKotlinException> {
            runBlocking {
                formTemplateQuery.forms(
                    GraphQLDoctor.ID(UUID.nameUUIDFromBytes("yeet".toByteArray())),
                    GraphQLFormTemplate.ID(UUID.nameUUIDFromBytes("seat".toByteArray())),
                    mockk()
                )
            }
        }
    }
}