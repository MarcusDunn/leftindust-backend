package com.leftindust.mockingbird.dao

import com.google.gson.JsonObject
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.impl.FormDataDaoImpl
import com.leftindust.mockingbird.dao.impl.repository.HibernateFormDataRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest(properties = ["spring.main.web-application-type=none"])
@Import(FormDataDaoImpl::class)
@Tag("Integration")
class FormDataDaoIntegrationTest(
    @Autowired private val patientRepository: HibernatePatientRepository,
    @Autowired private val formDataRepository: HibernateFormDataRepository,
    @Autowired private val formDataDao: FormDataDao,
) {

    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    fun `test adding form`() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val patient = patientRepository.save(EntityStore.patient("FormDataDaoIntegrationTest.test adding form"))
        val gqlid = GraphQLPatient.ID(patient.id!!)
        val form = JsonObject().apply {
            addProperty("hello", "world")
        }
        val formEntity = runBlocking {
            formDataDao.attachForm(
                patient = gqlid, form,
                mockk()
            )
        }
        assertEquals(runBlocking { formDataDao.getForms(patient = gqlid, mockk()) }.first().data, form)
        formDataRepository.deleteById(formEntity.id!!)
        patientRepository.delete(patient)
    }
}
