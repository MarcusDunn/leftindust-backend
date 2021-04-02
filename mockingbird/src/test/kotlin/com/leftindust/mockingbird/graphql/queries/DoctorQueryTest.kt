package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DoctorQueryTest {
    private val doctorDao = mockk<DoctorDao>()
    private val authContext = mockk<GraphQLAuthContext>()

    @Test
    fun getDoctorsByPatient() {
        val doctor = EntityStore.doctor("DoctorQueryTest.getDoctorsByPatient").apply {
            id = 2000
        }

        every { authContext.mediqAuthToken } returns mockk()

        val graphQLDoctor = GraphQLDoctor(doctor, doctor.id!!, authContext)

        coEvery { doctorDao.getByPatient(1000, authContext.mediqAuthToken) } returns mockk() {
            every { getOrThrow() } returns listOf(doctor)
        }

        val doctorQuery = DoctorQuery(doctorDao)

        val result = runBlocking { doctorQuery.doctors(pid = gqlID(1000), authContext = authContext) }

        assertEquals(listOf(graphQLDoctor), result)
    }

    @Test
    internal fun doctors() {
        val doctor = EntityStore.doctor("DoctorQueryTest.doctors").apply {
            id = 1000
        }

        every { authContext.mediqAuthToken } returns mockk()

        val graphQLDoctor = GraphQLDoctor(doctor, doctor.id!!, authContext)

        coEvery { doctorDao.getByDoctor(1000, any()) } returns mockk {
            every { getOrThrow() } returns doctor
        }

        val doctorQuery = DoctorQuery(doctorDao)

        val result = runBlocking { doctorQuery.doctors(dids = listOf(gqlID(1000)), authContext = authContext) }

        assertEquals(listOf(graphQLDoctor), result)
    }
}