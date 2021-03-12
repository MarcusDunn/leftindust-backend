package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Relationship
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@Transactional
@SpringBootTest
internal class ContactDaoTest(
    @Autowired private val contactDao: ContactDao
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getByPatient() {

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
            relationship = Relationship.Sibling,
        )
        patient.contacts = setOf(contact)
        val patientID = session.save(patient) as Long
        session.save(contact)

        val result = runBlocking { contactDao.getByPatient(patientID, FakeAuth.Valid.Token) }

        assertEquals(result.unwrap(), listOf(contact))
    }

    @Test
    fun `getByPatient with non-existing patient`() {
        val result = runBlocking { contactDao.getByPatient(-1, FakeAuth.Valid.Token).unwrapFailure() }
        assert(result is DoesNotExist)
    }
}