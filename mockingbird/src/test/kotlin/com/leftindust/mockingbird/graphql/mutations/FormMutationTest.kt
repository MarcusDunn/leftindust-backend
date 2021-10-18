package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.graphql.types.GraphQLFormTemplate
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class FormMutationTest {
    private val formDao = mockk<FormDao>()

    @Test
    fun addForm() {

        val formMutation = FormMutation(formDao)
        val form = EntityStore.graphQLFormInput("FormMutationTest")

        coEvery { formDao.addForm(form, any()) } returns Form(form).apply {
            id = UUID.nameUUIDFromBytes("rfgvha".toByteArray())
        }

        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result = runBlocking { formMutation.addForm(form, authContext = authContext) }
        assertEquals(form.name, result.name)
        assertEquals(form.sections.map { it.name }, result.sections.map { it.name })
    }

    @Test
    fun deleteForm() {
        val formMutation = FormMutation(formDao)
        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val uuid = UUID.nameUUIDFromBytes("rfgvha".toByteArray())
        val gqlUuid = GraphQLFormTemplate.ID(uuid)

        val mockk = mockk<Form>(relaxed = true) {
            every { id } returns uuid
        }

        coEvery { formDao.deleteForm(gqlUuid, any()) } returns mockk


        val result = runBlocking {
            formMutation.deleteForm(
                gqlUuid,
                authContext = authContext
            )
        }
        assertEquals(GraphQLFormTemplate(mockk, authContext), result)
    }
}