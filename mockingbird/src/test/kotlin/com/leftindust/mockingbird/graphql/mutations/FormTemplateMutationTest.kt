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
}