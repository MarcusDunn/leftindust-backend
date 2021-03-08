package com.leftindust.mediq.graphql.queries

import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.CustomResultException
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.graphql.types.GraphQLVisit
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.TimestampFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class VisitQueryTest(
    @Autowired private val visitQuery: VisitQuery,
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession


    @Test
    internal fun `test autowire`() {
        assertDoesNotThrow {
            visitQuery.hashCode()
        }
    }

    @Test
    fun getVisitsByPatient() {
        val patient = Patient(
            pid = 0,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val doctor = Doctor(
            did = 0,
            firstName = "Dan",
            lastName = "shervershani",
        )
        session.save(doctor)
        val visit = Visit(
            doctor = doctor,
            patient = patient,
            timeBooked = TimestampFaker(0).create(),
            timeOfVisit = TimestampFaker(1).create(),
            icdFoundationCode = FoundationIcdCode("1"),

            )
        session.save(visit)

        val result = runBlocking { visitQuery.getVisitsByPatient(gqlID(patient.pid), FakeAuth.Valid.Context) }

        assertEquals(listOf(GraphQLVisit(visit, visit.id!!, FakeAuth.Valid.Context)), result)
    }

    @Test
    fun `getVisitsByPatient with no such patient`() {
        val exception = assertThrows(CustomResultException::class.java) {
            runBlocking { visitQuery.getVisitsByPatient(gqlID(-1), FakeAuth.Valid.Context) }
        }

        assert(exception.message!!.contains("DoesNotExist"))

    }
}