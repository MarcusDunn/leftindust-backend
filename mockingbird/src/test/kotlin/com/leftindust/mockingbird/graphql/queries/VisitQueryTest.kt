package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.TimestampFaker
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
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)
        val doctor = Doctor(
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

        val result = runBlocking { visitQuery.getVisitsByPatient(gqlID(patient.id!!), FakeAuth.Valid.Context) }

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