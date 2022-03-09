package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.impl.repository.HibernateContactRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class ContactDaoImplTest {

    private val authorizer = mockk<Authorizer>()
    private val patientRepository = mockk<HibernateContactRepository>()

    @Test
    fun getByPatient() {
        val emergencyContact = mockk<EmergencyContact>()
        val patientId = UUID.randomUUID()


        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed
        every { patientRepository.getAllByPatient_Id(patientId) } returns setOf(emergencyContact)

        val contactDaoImpl = ContactDaoImpl(authorizer, patientRepository)
        val actual = runBlocking { contactDaoImpl.getPatientContacts(GraphQLPatient.ID(patientId), mockk()) }

        assertIterableEquals(listOf(emergencyContact), actual)
    }
}