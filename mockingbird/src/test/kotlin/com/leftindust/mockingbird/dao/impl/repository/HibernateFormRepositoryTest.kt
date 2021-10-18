package com.leftindust.mockingbird.dao.impl.repository

import integration.util.EntityStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest(properties = ["spring.main.web-application-type=none"])
@Tag("Integration")
class HibernateFormRepositoryTest(
    @Autowired private val formRepository: HibernateFormRepository
) {

    @Test
    internal fun `test insert form template`() {
        val form = formRepository.save(EntityStore.form("FormTemplateMutationTest.test insert form template"))
        val result = formRepository.getById(form.id!!)
        assertEquals(form, result)
    }

    @Test
    internal fun `delete form template`() {
        val form = formRepository.save(EntityStore.form("FormTemplateMutationTest.test insert form template"))
        assertDoesNotThrow { formRepository.getById(form.id!!) }
        formRepository.delete(form)
        assertThrows<Exception> { formRepository.getById(form.id!!) }
    }
}