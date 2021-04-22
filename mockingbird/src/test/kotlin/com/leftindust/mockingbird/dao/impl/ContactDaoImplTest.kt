package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.impl.repository.HibernateContactRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernatePatientRepository
import com.leftindust.mockingbird.extensions.Authorization
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test

internal class ContactDaoImplTest {

    private val authorizer = mockk<Authorizer>()
    private val patientRepository = mockk<HibernateContactRepository>()

    @Test
    fun getByPatient() {
        val emergencyContact = mockk<EmergencyContact>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getAllByPatient_Id(any()) } returns setOf(emergencyContact)

        val contactDaoImpl = ContactDaoImpl(authorizer, patientRepository)
        val actual = runBlocking { contactDaoImpl.getByPatient(1000L, mockk()) }

        assertIterableEquals(listOf(emergencyContact), actual)
    }
}