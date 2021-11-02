package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.FormDataDao
import com.leftindust.mockingbird.dao.entity.Form
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

internal class FormMutationTest {
    private val formDao = mockk<FormDao>()
    private val formDataDao = mockk<FormDataDao>()


    @Test
    fun addForm() {

        val formMutation = FormMutation(formDao, formDataDao)
        val form = EntityStore.graphQLFormInput("FormMutationTest")

        coEvery { formDao.addForm(form, any()) } returns Form(form).apply {
            id = UUID.nameUUIDFromBytes("rfgvha".toByteArray())
        }

        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result = runBlocking { formMutation.addFormTemplate(form, authContext = authContext) }
        assertEquals(form.name, result.name)
        assertEquals(form.sections.map { it.name }, result.sections.map { it.name })
    }


}