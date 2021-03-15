package com.leftindust.mockingbird.graphql.queries

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.DoctorDao
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
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
        val mockkDoctor = mockk<Doctor>(relaxed = true) {
            every { id } returns 2000
            every { workPhone } returns null
            every { cellPhone } returns null
            every { pagerNumber } returns null
            every { homePhone } returns null
        }

        every { authContext.mediqAuthToken } returns mockk()

        val graphQLDoctor = GraphQLDoctor(mockkDoctor, mockkDoctor.id!!, authContext)

        coEvery { doctorDao.getByPatient(1000, authContext.mediqAuthToken) } returns mockk() {
            every { getOrThrow() } returns listOf(mockkDoctor)
        }

        val doctorQuery = DoctorQuery(doctorDao)

        val result = runBlocking { doctorQuery.getDoctorsByPatient(gqlID(1000), authContext) }

        assertEquals(listOf(graphQLDoctor), result)
    }

    @Test
    fun doctor() {
        val mockkDoctor = mockk<Doctor>(relaxed = true) {
            every { id } returns 2000
            every { workPhone } returns null
            every { cellPhone } returns null
            every { pagerNumber } returns null
            every { homePhone } returns null
        }

        every { authContext.mediqAuthToken } returns mockk()

        val graphQLDoctor = GraphQLDoctor(mockkDoctor, mockkDoctor.id!!, authContext)

        coEvery { doctorDao.getByDoctor(1000, any()) } returns mockk {
            every { getOrThrow() } returns mockkDoctor
        }

        val doctorQuery = DoctorQuery(doctorDao)

        val result = runBlocking { doctorQuery.doctor(gqlID(1000), authContext) }

        assertEquals(graphQLDoctor, result)


    }
}