package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.DoctorPatient
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.CustomResultException
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class DoctorQueryTest(
    @Autowired private val doctorQuery: DoctorQuery
) {

    @Autowired
    lateinit var sessionFactory: SessionFactory

    private val session: Session
        get() = sessionFactory.currentSession


    @Test
    fun getDoctorsByPatient() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Shervershani",
        )
        session.save(patient)
        session.save(doctor)
        val doctorPatient = DoctorPatient(
            patient = patient,
            doctor = doctor,
        )
        session.save(doctorPatient)

        val result = runBlocking { doctorQuery.getDoctorsByPatient(gqlID(patient.id!!), FakeAuth.Valid.Context) }

        assertEquals(listOf(GraphQLDoctor(doctor, doctor.id!!, FakeAuth.Valid.Context)), result)
    }

    @Test
    fun `getDoctorsByPatient with no such patient`() {
        assertThrows(CustomResultException::class.java) {
            runBlocking { doctorQuery.getDoctorsByPatient(gqlID(0), FakeAuth.Valid.Context) }
        }
    }

    @Test
    fun `getDoctorsByPatient with no doctors`() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking { doctorQuery.getDoctorsByPatient(gqlID(patient.id!!), FakeAuth.Valid.Context) }
        assertEquals(emptyList<GraphQLDoctor>(), result)
    }

    @Test
    fun doctor() {
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Shervershani",
        )
        val doctorId = session.save(doctor) as Long

        val result = runBlocking { doctorQuery.doctor(gqlID(doctorId), FakeAuth.Valid.Context) }

        assertEquals(GraphQLDoctor(doctor, doctor.id!!, FakeAuth.Valid.Context), result)
    }

    @Test
    fun `doctor with no such doctor`() {
        val exception = assertThrows(CustomResultException::class.java) {
            runBlocking { doctorQuery.doctor(gqlID(0), FakeAuth.Valid.Context) }
        }

        assert(exception.message!!.contains("DoesNotExist"))
    }
}