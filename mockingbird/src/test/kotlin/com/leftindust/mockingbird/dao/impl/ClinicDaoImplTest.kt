package com.leftindust.mockingbird.dao.impl

import com.expediagroup.graphql.generator.scalars.ID
import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.Crud
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.Tables
import com.leftindust.mockingbird.dao.entity.Action
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ClinicDaoImplTest {
    private val clinicRepository = mockk<HibernateClinicRepository>()
    private val sessionFactory = mockk<SessionFactory>()
    private val authorizer = mockk<Authorizer>()

    @Test
    fun addClinic() {
        val mockkClinic = mockk<Clinic>()

        every { clinicRepository.save(any()) } returns mockkClinic

        every { sessionFactory.currentSession } returns mockk() {
            every { get(Doctor::class.java, 10L) } returns mockk()
        }


        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), requester) } returns Authorization.Allowed

        val mockkGqlClinicInput = GraphQLClinicInput(
            name = "name",
            address = mockk(relaxed = true) {
                every { country } returns GraphQLCountry.Canada
                every { province } returns "BritishColumbia"
            },
            doctors = listOf(ID("10"))
        )

        val clinicDao = ClinicDaoImpl(clinicRepository, sessionFactory, authorizer)

        val result = runBlocking { clinicDao.addClinic(mockkGqlClinicInput, requester) }

        assertEquals(mockkClinic, result)
    }

    @Test
    fun `addClinic with insufficient perms`() {
        val mockkClinic = mockk<Clinic>()

        every { clinicRepository.save(any()) } returns mockkClinic

        every { sessionFactory.currentSession } returns mockk() {
            every { get(Doctor::class.java, 10L) } returns mockk()
        }

        val mockkGqlClinicInput = GraphQLClinicInput(
            name = "name",
            address = mockk(relaxed = true) {
                every { country } returns GraphQLCountry.Canada
                every { province } returns "BritishColumbia"
            },
            doctors = listOf(ID("10"))
        )

        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), requester) } returns Authorization.Denied

        val clinicDao = ClinicDaoImpl(clinicRepository, sessionFactory, authorizer)

        assertThrows<NotAuthorizedException> {
            runBlocking { clinicDao.addClinic(mockkGqlClinicInput, requester) }
        }
    }
}
