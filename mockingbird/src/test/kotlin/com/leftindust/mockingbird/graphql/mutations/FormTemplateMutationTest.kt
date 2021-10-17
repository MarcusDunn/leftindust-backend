package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.dao.impl.repository.HibernateFormRepository
import integration.util.EntityStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest(properties = ["spring.main.web-application-type=none"])
@Tag("Integration")
class FormTemplateMutationTest(
    @Autowired private val formRepository: HibernateFormRepository
) {

    @Test
    internal fun `test insert form template`() {
        val form = formRepository.save(EntityStore.form("FormTemplateMutationTest.test insert form template"))
        val result = formRepository.getById(form.id!!)
        assertEquals(form, result)
    }

    @Test
    internal fun `update form template name`() {
        val form = formRepository.save(EntityStore.form("FormTemplateMutationTest.test insert form template"))
        val newName = form.name + "new name!!"
        form.name = newName
        val updatedForm = formRepository.save(form)
        val result = formRepository.getById(form.id!!)
        assertEquals(updatedForm, result)
        assertEquals(updatedForm.name, newName)
    }

    @Test
    internal fun `update form template section name`() {
        val form = formRepository.save(EntityStore.form("FormTemplateMutationTest.test insert form template"))
        val newName = "new name!"
        form.sections.find { it.number == 1 }!!.name = newName
        val updatedForm = formRepository.save(form)
        val result = formRepository.getById(form.id!!)
        assertEquals(updatedForm, result)
        assertEquals(updatedForm.sections.find { it.number == 1 }!!.name, newName)
    }
}