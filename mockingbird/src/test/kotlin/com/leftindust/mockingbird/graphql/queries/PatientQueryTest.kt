package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.patient.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import com.leftindust.mockingbird.graphql.types.search.example.GraphQLPatientExample
import com.leftindust.mockingbird.graphql.types.search.filter.CaseAgnosticStringFilter
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class PatientQueryTest {
    private val patientDao = mockk<PatientDao>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun patient() {
        val patientID = UUID.randomUUID()

        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns patientID
        }
        coEvery { patientDao.getPatientsByPids(listOf(GraphQLPatient.ID(patientID)), any()) } returns listOf(
            mockkPatient
        )
        every { authContext.mediqAuthToken } returns mockk()
        val graphQLPatient = GraphQLPatient(mockkPatient, authContext)
        val patientQuery = PatientQuery(patientDao)
        val result =
            runBlocking { patientQuery.patientsByPid(listOf(GraphQLPatient.ID(patientID)), authContext = authContext) }
        assertEquals(listOf(graphQLPatient), result)
    }

    @Test
    fun patients() {
        val patientID = UUID.randomUUID()

        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns patientID
        }
        every { authContext.mediqAuthToken } returns mockk()
        coEvery { patientDao.getMany(any(), any(), any()) } returns listOf(
            mockkPatient,
            mockkPatient,
            mockkPatient,
            mockkPatient,
            mockkPatient
        )
        val patientQuery = PatientQuery(patientDao)
        val graphQLPatient = GraphQLPatient(mockkPatient, authContext)
        val result = runBlocking { patientQuery.patientsByRange(GraphQLRangeInput(0, 5), authContext = authContext) }
        assertEquals((0 until 5).map { graphQLPatient }, result)
    }

    @Test
    fun patientsByExample() {
        val patientID = UUID.randomUUID()

        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns patientID
        }
        every { authContext.mediqAuthToken } returns mockk()
        coEvery { patientDao.searchByExample(any(), any()) } returns listOf(
            mockkPatient,
            mockkPatient,
            mockkPatient,
            mockkPatient,
            mockkPatient
        )
        val patientQuery = PatientQuery(patientDao)
        val graphQLPatient = GraphQLPatient(mockkPatient, authContext)
        val result = runBlocking {
            patientQuery.patientsByExample(
                GraphQLPatientExample(
                    firstName = CaseAgnosticStringFilter(eq = "Marcus", strict = true),
                    strict = true
                ),
                authContext = authContext
            )
        }
        assertEquals((0 until 1).map { graphQLPatient }, result)
    }
}