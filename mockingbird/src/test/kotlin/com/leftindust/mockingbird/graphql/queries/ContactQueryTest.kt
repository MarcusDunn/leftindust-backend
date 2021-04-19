package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.ContactDao
import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ContactQueryTest {
    private val contactDao = mockk<ContactDao>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun getContactsByPatient() {
        val mockkContact = mockk<EmergencyContact>(relaxed = true)

        every { authContext.mediqAuthToken } returns mockk()

        val graphQLEmergencyContact = GraphQLEmergencyContact(mockkContact, authContext)

        coEvery { contactDao.getByPatient(1000L, any()) } returns listOf(mockkContact)

        val contactQuery = ContactQuery(contactDao)

        val result = runBlocking { contactQuery.getContactsByPatient(gqlID(1000), authContext) }

        assertEquals(listOf(graphQLEmergencyContact), result)
    }
}