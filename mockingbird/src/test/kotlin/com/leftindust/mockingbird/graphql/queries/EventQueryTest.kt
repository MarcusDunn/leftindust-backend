package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.EventDao
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.Success
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventQueryTest {
    private val eventDao = mockk<EventDao>()
    private val patientDao = mockk<PatientDao>()
    private val doctorDao = mockk<DoctorDao>()

    @Test
    internal fun getByDoctor() {
        val expected = mockk<Doctor> {
            every { id } returns 1000
            every { schedule } returns mockk {
                every { events } returns mutableSetOf(mockk(relaxed = true) {
                    every { id } returns 4000L
                })
            }
        }
        coEvery { doctorDao.getByDoctor(1000, any()) } returns expected

        val eventQuery = EventQuery(eventDao, patientDao, doctorDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }

        val result =
            runBlocking { eventQuery.events(doctors = listOf(gqlID(1000)), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(expected.schedule.events.map {
            GraphQLEvent(
                event = it,
                doctors = listOf(gqlID(1000)),
                patients = emptyList(),
                authContext = graphQLAuthContext
            )
        }, result)
    }

    @Test
    internal fun getByPatient() {
        val expected = mockk<Patient> {
            every { id } returns 2000L
            every { schedule } returns mockk {
                every { events } returns mutableSetOf(mockk(relaxed = true) {
                    every { id } returns 4000L
                })
            }
        }
        coEvery { patientDao.getByPID(2000L, any()) } returns Success(expected)

        val eventQuery = EventQuery(eventDao, patientDao, doctorDao)

        val graphQLAuthContext = mockk<GraphQLAuthContext> {
            every { mediqAuthToken } returns mockk()
        }
        val result =
            runBlocking { eventQuery.events(patients = listOf(gqlID(2000)), graphQLAuthContext = graphQLAuthContext) }

        assertEquals(expected.schedule.events.map {
            GraphQLEvent(
                event = it,
                doctors = emptyList(),
                patients = listOf(gqlID(2000)),
                authContext = graphQLAuthContext
            )
        }, result)
    }
}