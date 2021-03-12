package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLEmergencyContact
import com.leftindust.mockingbird.graphql.types.GraphQLPerson
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class ContactQueryTest(
    @Autowired private val contactQuery: ContactQuery
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getContactsByPatient() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )

        val contact = EmergencyContact(
            cid = 12,
            patient = patient,
            firstName = "Dan",
            lastName = "Shervershani",
            relationship = Relationship.Sibling
        )
        patient.contacts = setOf(contact)
        val patientID = session.save(patient) as Long
        session.save(contact)


        val result = runBlocking { contactQuery.getContactsByPatient(gqlID(patientID), FakeAuth.Valid.Context) }

        assertEquals(listOf(GraphQLEmergencyContact(contact, FakeAuth.Valid.Context)), result)
    }

    @Test
    fun `getContactsByPatient with no such patient`() {
        assertThrows(CustomResultException::class.java) {
            runBlocking { contactQuery.getContactsByPatient(gqlID(0), FakeAuth.Valid.Context) }
        }
    }

    @Test
    fun `getContactsByPatient with empty contacts`() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long


        val result = runBlocking { contactQuery.getContactsByPatient(gqlID(patientID), FakeAuth.Valid.Context) }

        assertEquals(result, emptyList<GraphQLPerson>())
    }
}