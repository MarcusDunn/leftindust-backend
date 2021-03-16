package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.PatientDao
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.input.GraphQLRangeInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PatientQueryTest {
    private val patientDao = mockk<PatientDao>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun patient() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }
        coEvery { patientDao.getByPID(1000, any()) } returns mockk() {
            every { getOrThrow() } returns mockkPatient
        }
        every { authContext.mediqAuthToken } returns mockk()
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val patientQuery = PatientQuery(patientDao)
        val result = runBlocking { patientQuery.patient(gqlID(1000), authContext) }
        assertEquals(graphQLPatient, result)
    }

    @Test
    fun patients() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }
        every { authContext.mediqAuthToken } returns mockk()
        coEvery { patientDao.getMany(0, 5, any(), any()) } returns mockk() {
            every { getOrThrow() } returns listOf(mockkPatient, mockkPatient, mockkPatient, mockkPatient, mockkPatient)
        }
        val patientQuery = PatientQuery(patientDao)
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val result = runBlocking { patientQuery.patients(GraphQLRangeInput(0, 5), authContext = authContext) }
        assertEquals((0 until 5).map { graphQLPatient }, result)
    }

    @Test
    fun patientsGrouped() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }
        coEvery { patientDao.getManyGroupedBySorted(0, 3, any(), any()) } returns mockk() {
            every { getOrThrow() } returns mapOf("a" to listOf(mockkPatient, mockkPatient, mockkPatient))
        }
        every { authContext.mediqAuthToken } returns mockk()
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val patientQuery = PatientQuery(patientDao)
        val result = runBlocking { patientQuery.patientsGrouped(GraphQLRangeInput(0, 3), authContext = authContext) }
        val expected = PatientQuery.GraphQLPatientGroupedList(mapOf("a" to (0 until 3).map { graphQLPatient }))
        assertEquals(expected, result)
    }

    @Test
    fun searchPatientsByName() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }
        coEvery { patientDao.searchByName("hello", any()) } returns mockk() {
            every { getOrThrow() } returns listOf(mockkPatient)
        }
        val patientQuery = PatientQuery(patientDao)
        every { authContext.mediqAuthToken } returns mockk()
        val result = runBlocking { patientQuery.searchPatientsByName("hello", authContext) }
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        assertEquals(listOf(graphQLPatient), result)
    }

    @Test
    fun searchPatient() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
            every { homePhone } returns null
            every { cellPhone } returns null
            every { workPhone } returns null
        }
        every { authContext.mediqAuthToken } returns mockk()
        coEvery { patientDao.searchByExample(any(), any()) } returns mockk() {
            every { getOrThrow() } returns listOf(mockkPatient)
        }
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val patientQuery = PatientQuery(patientDao)
        val result = runBlocking { patientQuery.searchPatient(mockk(), authContext) }
        assertEquals(listOf(graphQLPatient), result)
    }
}