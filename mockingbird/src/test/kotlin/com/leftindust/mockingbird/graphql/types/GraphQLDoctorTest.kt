package com.leftindust.mockingbird.graphql.types

import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.VisitDao
import com.leftindust.mockingbird.dao.entity.*
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@SpringBootTest
@Transactional
internal class GraphQLDoctorTest(
    @Autowired private val patientDao: PatientDao,
    @Autowired private val visitDao: VisitDao,
    @Autowired private val doctorDao: DoctorDao,
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun patients() {
        val doctorEntity = Doctor(
            firstName = "Marcus",
            lastName = "Dunn",
        ).also { session.save(it) }
        val patientEntity = Patient(
            firstName = "Boris",
            lastName = "Vaselchov",
            sex = Sex.Male
        ).also { session.save(it) }
        val doctorPatient = DoctorPatient(
            doctor = doctorEntity,
            patient = patientEntity,
        ).also { session.save(it) }
        doctorEntity.patients = setOf(doctorPatient)
        patientEntity.doctors = setOf(doctorPatient)
        val doctor = GraphQLDoctor(doctorEntity, doctorEntity.id!!, FakeAuth.Valid.Context)

        val result = runBlocking { doctor.patients(patientDao) }

        assertEquals(listOf(GraphQLPatient(patientEntity, patientEntity.id!!, FakeAuth.Valid.Context)), result)
    }

    @Test
    fun visits() {
        val doctorEntity = Doctor(
            firstName = "Marcus",
            lastName = "Dunn",
        ).also { session.save(it) }
        val patientEntity = Patient(
            firstName = "Boris",
            lastName = "Vaselchov",
            sex = Sex.Male
        ).also { session.save(it) }
        val visitEntity = Visit(
            timeBooked = Timestamp.valueOf("2020-05-01 00:00:00"),
            timeOfVisit = Timestamp.valueOf("2020-06-01 00:00:00"),
            doctor = doctorEntity,
            patient = patientEntity,
            icdFoundationCode = FoundationIcdCode("1"),
        ).also { session.save(it) }
        val doctor = GraphQLDoctor(doctorEntity, doctorEntity.id!!, FakeAuth.Valid.Context)

        val result = runBlocking { doctor.visits(visitDao) }

        assertEquals(listOf(GraphQLVisit(visitEntity, visitEntity.id!!, FakeAuth.Valid.Context)), result)
    }

    @Test
    fun schedule() {
        val doctorEntity = Doctor(
            firstName = "Marcus",
            lastName = "Dunn",
            schedule = Schedule(
                scheduleId = 1,
                events = setOf(
                    Event(
                        title = "say hello",
                        description = "attempt to make friends",
                        startTime = Timestamp.valueOf("2020-05-01 00:00:00"),
                        recurrenceRule = RecurrenceRule(Recurrence.Builder(Frequency.MONTHLY).build()),
                        durationMillis = 10,
                    )
                )
            )
        ).also { session.save(it) }
        val doctor = GraphQLDoctor(doctorEntity, doctorEntity.id!!, FakeAuth.Valid.Context)

        val time2020 = GraphQLTime(Timestamp.valueOf("2020-01-01 00:00:00"))
        val time2021 = GraphQLTime(Timestamp.valueOf("2021-01-01 00:00:00"))

        val events = runBlocking { doctor.schedule(doctorDao, time2020, time2021) }

        assertEquals(9, events.size)
    }
}