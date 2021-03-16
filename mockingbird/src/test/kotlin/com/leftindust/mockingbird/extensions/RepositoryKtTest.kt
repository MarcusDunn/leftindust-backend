package com.leftindust.mockingbird.extensions

import com.leftindust.mockingbird.dao.entity.Patient
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import javax.persistence.EntityNotFoundException

internal class RepositoryKtTest {
    private val jpaRepository = mockk<JpaRepository<Patient, Long>>()


    @Test
    fun `getOneOrNull when one exists`() {
        val mockkPatient = mockk<Patient>()

        every { jpaRepository.getOne(1000) } returns mockkPatient

        assertEquals(mockkPatient, jpaRepository.getOneOrNull(1000))
    }

    @Test
    fun `getOneOrNull when one does not exists`() {
        every { jpaRepository.getOne(1000) } throws mockk<JpaObjectRetrievalFailureException> {
            every { cause } returns mockk<EntityNotFoundException>()
        }

        assertEquals(null, jpaRepository.getOneOrNull(1000))
    }

    @Test
    fun `getOneOrNull getOne throws non-doesNotExist Error`() {
        every { jpaRepository.getOne(1000) } throws mockk<JpaObjectRetrievalFailureException> {
            every { cause } returns mockk<Exception>()
        }

        assertThrows<Throwable> {
            jpaRepository.getOneOrNull(1000)
        }
    }
}