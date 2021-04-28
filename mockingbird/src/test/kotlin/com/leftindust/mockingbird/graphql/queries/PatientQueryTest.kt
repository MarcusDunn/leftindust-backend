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
        }
        coEvery { patientDao.getPatientsByPids(listOf(gqlID(1000)), any()) } returns listOf(mockkPatient)
        every { authContext.mediqAuthToken } returns mockk()
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val patientQuery = PatientQuery(patientDao)
        val result = runBlocking { patientQuery.patients(pids = listOf(gqlID(1000)), authContext = authContext) }
        assertEquals(listOf(graphQLPatient), result)
    }

    @Test
    fun patients() {
        val mockkPatient = mockk<Patient>(relaxed = true) {
            every { id } returns 1000L
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
        val graphQLPatient = GraphQLPatient(mockkPatient, mockkPatient.id!!, authContext)
        val result = runBlocking { patientQuery.patients(GraphQLRangeInput(0, 5), authContext = authContext) }
        assertEquals((0 until 5).map { graphQLPatient }, result)
    }
}