package com.leftindust.mockingbird.dao.entity

import integration.util.EntityStore
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest(properties = ["spring.main.web-application-type=none"])
@Tag("Integration")
class AssignedFormIntegrationTest(@Autowired private val entityManager: TestEntityManager) {
    @Test
    fun `check can add assigned form`() {
        assertDoesNotThrow {
            entityManager.entityManager.persist(
                AssignedForm(
                    EntityStore.form("AssignedFormIntegrationTest.check can add assigned form"),
                    EntityStore.patient("AssignedFormIntegrationTest.check can add assigned form"),
                )
            )
        }
    }
}