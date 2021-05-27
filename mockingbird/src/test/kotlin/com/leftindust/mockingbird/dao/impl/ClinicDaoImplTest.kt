package com.leftindust.mockingbird.dao.impl

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.auth.NotAuthorizedException
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLClinic
import com.leftindust.mockingbird.graphql.types.GraphQLCountry
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLClinicInput
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class ClinicDaoImplTest {
    private val clinicRepository = mockk<HibernateClinicRepository>()
    private val doctorRepository = mockk<HibernateDoctorRepository>()
    private val sessionFactory = mockk<SessionFactory>()
    private val authorizer = mockk<Authorizer>()

    @Test
    fun addClinic() {
        val doctorID = UUID.randomUUID()


        val mockkClinic = mockk<Clinic>()

        every { clinicRepository.save(any()) } returns mockkClinic

        every { sessionFactory.currentSession } returns mockk() {
            every { get(Doctor::class.java, doctorID) } returns mockk()
        }


        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), requester) } returns Authorization.Allowed

        val mockkGqlClinicInput = GraphQLClinicInput(
            name = "name",
            address = mockk(relaxed = true) {
                every { country } returns GraphQLCountry.Canada
                every { province } returns "BritishColumbia"
            },
            doctors = listOf(GraphQLDoctor.ID(doctorID))
        )

        val clinicDao = ClinicDaoImpl(clinicRepository, doctorRepository, sessionFactory, authorizer)

        val result = runBlocking { clinicDao.addClinic(mockkGqlClinicInput, requester) }

        assertEquals(mockkClinic, result)
    }

    @Test
    fun `addClinic with insufficient perms`() {
        val doctorID = UUID.randomUUID()

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
            doctors = listOf(GraphQLDoctor.ID(doctorID))
        )

        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), requester) } returns Authorization.Denied

        val clinicDao = ClinicDaoImpl(clinicRepository, doctorRepository, sessionFactory, authorizer)

        assertThrows<NotAuthorizedException> {
            runBlocking { clinicDao.addClinic(mockkGqlClinicInput, requester) }
        }
    }

    @Test
    fun editClinic() {
        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), requester) } returns Authorization.Allowed

        val clinicId = UUID.randomUUID()
        val mockkGqlClinicInput = GraphQLClinicEditInput(
            cid = GraphQLClinic.ID(clinicId),
            address = mockk(relaxed = true) {
                every { country } returns GraphQLCountry.Canada
                every { province } returns "BritishColumbia"
            },
        )

        every { sessionFactory.currentSession } returns mockk()

        val mockkClinic = mockk<Clinic> {
            every { setByGqlInput(mockkGqlClinicInput, any()) } just runs
        }

        every { clinicRepository.getById(clinicId) } returns mockkClinic

        val clinicDao = ClinicDaoImpl(clinicRepository, doctorRepository, sessionFactory, authorizer)

        val result = runBlocking { clinicDao.editClinic(mockkGqlClinicInput, requester) }

        assertEquals(mockkClinic, result)
    }

    @Test
    fun getByDoctor() {
        val doctorID = UUID.randomUUID()

        val requester = mockk<MediqToken>()

        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val mockkClinic = mockk<Clinic>()

        val mockkDoctor = mockk<Doctor>()

        every { doctorRepository.getById(doctorID) } returns mockkDoctor

        every { clinicRepository.getAllByDoctorsContains(mockkDoctor) } returns listOf(mockkClinic)

        val clinicDao = ClinicDaoImpl(clinicRepository, doctorRepository, sessionFactory, authorizer)


        val result = runBlocking { clinicDao.getByDoctor(GraphQLDoctor.ID(doctorID), requester) }

        assertEquals(listOf(mockkClinic), result)
    }
}
