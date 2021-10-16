package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.auth.Authorizer
import com.leftindust.mockingbird.auth.MediqToken
import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.impl.ClinicDaoImpl
import com.leftindust.mockingbird.dao.impl.repository.HibernateClinicRepository
import com.leftindust.mockingbird.dao.impl.repository.HibernateDoctorRepository
import com.leftindust.mockingbird.extensions.Authorization
import com.leftindust.mockingbird.graphql.types.GraphQLDoctor
import com.ninjasquad.springmockk.MockkBean
import integration.util.EntityStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest(properties = ["spring.main.web-application-type=none"])
@Import(ClinicDaoImpl::class)
@Tag("Integration")
class ClinicDaoIntegrationTest(
    @Autowired private val clinicDao: ClinicDao,
    @Autowired private val doctorRepository: HibernateDoctorRepository,
    @Autowired private val clinicRepository: HibernateClinicRepository,
) {
    @MockkBean
    private lateinit var authorizer: Authorizer

    @Test
    internal fun `test get clinic by doctor`() {
        coEvery { authorizer.getAuthorization(any(), any()) } returns Authorization.Allowed

        val clinic = clinicRepository.save(
            Clinic(
                name = "name",
                address = EntityStore.address("ClinicDaoTest.test get clinic by doctor"),
                doctors = mutableSetOf()
            )
        )

        val doctor = doctorRepository.save(EntityStore.doctor("ClinicDaoTest.test get clinic by doctor"))

        doctor.clinics.add(clinic)
        clinic.doctors.add(doctor)

        val requester = mockk<MediqToken>() {
            every { uid } returns "admin"
        }

        assertEquals(doctor, doctorRepository.getById(doctor.id!!))
        assertEquals(doctor.clinics, doctorRepository.getById(doctor.id!!).clinics)

        val result = runBlocking { clinicDao.getByDoctor(GraphQLDoctor.ID(doctor.id!!), requester) }

        assertEquals(doctor.clinics, result)
        assertEquals(result.first().doctors, setOf(doctor))
    }
}