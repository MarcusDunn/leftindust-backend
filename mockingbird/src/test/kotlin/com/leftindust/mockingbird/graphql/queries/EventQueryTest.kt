package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class EventQueryTest {
    private val eventDao = mockk<EventDao>()
    private val patientDao = mockk<PatientDao>()
    private val doctorDao = mockk<DoctorDao>()

    @Test
    internal fun getByDoctor() {
        val doctorID = UUID.randomUUID()
        val eventID = UUID.randomUUID()

        val expected = mockk<Doctor> {
            every { id } returns doctorID

            every { events } returns mutableSetOf(mockk(relaxed = true) {
                every { id } returns eventID
            })

        }
        coEvery { doctorDao.getByDoctor(GraphQLDoctor.ID(doctorID), any()) } returns expected

        val eventQuery = EventQuery(eventDao, patientDao, doctorDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val result =
            runBlocking {
                eventQuery.events(
                    doctors = listOf(GraphQLDoctor.ID(doctorID)),
                    graphQLAuthContext = graphQLAuthContext
                )
            }

        assertEquals(expected.events.map {
            GraphQLEvent(
                event = it,
                authContext = graphQLAuthContext
            )
        }, result)
    }

    @Test
    internal fun getByPatient() {
        val patientID = UUID.randomUUID()
        val eventID = UUID.randomUUID()

        val expected = mockk<Patient> {
            every { id } returns patientID
                every { events } returns mutableSetOf(mockk(relaxed = true) {
                    every { id } returns eventID
                })

        }
        coEvery { patientDao.getByPID(GraphQLPatient.ID(patientID), any()) } returns expected

        val eventQuery = EventQuery(eventDao, patientDao, doctorDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result =
            runBlocking {
                eventQuery.events(
                    patients = listOf(GraphQLPatient.ID(patientID)),
                    graphQLAuthContext = graphQLAuthContext
                )
            }

        assertEquals(expected.events.map {
            GraphQLEvent(
                event = it,
                authContext = graphQLAuthContext
            )
        }, result)
    }
}