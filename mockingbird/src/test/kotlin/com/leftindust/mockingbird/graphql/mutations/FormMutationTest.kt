package com.leftindust.mockingbird.graphql.mutations

import com.google.gson.JsonParser
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.FormDao
import com.leftindust.mockingbird.dao.FormDataDao
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.entity.FormData
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
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
        val result = runBlocking { formMutation.addSurveyTemplate(form, authContext = authContext) }
        assertEquals(form.name, result.name)
        assertEquals(form.sections.map { it.name }, result.sections.map { it.name })
    }

    @Test
    fun `test submitSurvey`() {
        val formMutation = FormMutation(formDao, formDataDao)
        //language=Json
        val formString = """{"hello":"world"}"""
        val form = JsonParser.parseString(formString)

        val mockkGqlPatient = mockk<GraphQLPatient>(relaxed = true)
        val mockkPatient = mockk<Patient>(relaxed = true)

        coEvery { formDataDao.attachForm(mockkGqlPatient.pid, form, any()) } returns FormData(form, mockkPatient)

        val authContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result = runBlocking { formMutation.submitSurvey(mockkGqlPatient.pid, surveyJson = formString, authContext = authContext) }
        assertEquals(formString, result.data)
    }
}