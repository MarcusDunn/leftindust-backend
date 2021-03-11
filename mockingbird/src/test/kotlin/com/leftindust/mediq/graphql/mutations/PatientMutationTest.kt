package com.leftindust.mediq.graphql.mutations

import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.getOrNull
import com.leftindust.mediq.extensions.getOrThrow
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.graphql.queries.PatientQuery
import com.leftindust.mediq.graphql.types.GraphQLDoctor
import com.leftindust.mediq.graphql.types.GraphQLPatient
import com.leftindust.mediq.graphql.types.input.GraphQLPatientInput
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class PatientMutationTest(
    @Autowired private val patientMutation: PatientMutation,
    @Autowired private val patientQuery: PatientQuery,
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession


    @Test
    fun addDoctorToPatient() {
        val patient = Patient(
            pid = 12,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        ).also { session.save(it) }
        val doctor = Doctor(
            firstName = "Daddy",
            lastName = "Dan",
        )
        val doctorId = session.save(doctor) as Long

        val result = runBlocking {
            patientMutation.addDoctorToPatient(
                patientById = ID(patient.pid.toString()),
                doctorById = gqlID(doctorId),
                FakeAuth.Valid.Context
            )
        }

        val expected = GraphQLPatient(patient, FakeAuth.Valid.Context)

        assertEquals(expected, result)
    }


    @Test
    fun `addDoctorToPatient persists`(@Autowired doctorDao: DoctorDao) {
        runBlocking {
            val patient = Patient(
                pid = 12,
                firstName = "Marcus",
                lastName = "Dunn",
                sex = Sex.Male
            ).also { session.save(it) }
            val doctor = Doctor(
                firstName = "Daddy",
                lastName = "Dan",
            )
            val doctorId = session.save(doctor) as Long
            patientMutation.addDoctorToPatient(
                patientById = ID(patient.pid.toString()),
                doctorById = gqlID(doctorId),
                FakeAuth.Valid.Context
            )

            val result = runBlocking {
                patientQuery.patient(ID(patient.pid.toString()), FakeAuth.Valid.Context)
            }

            assert(result.doctors(doctorDao).contains(GraphQLDoctor(doctor, doctor.id!!, FakeAuth.Valid.Context))) {
                result.doctors(
                    doctorDao
                )
            }
        }
    }

    @Test
    fun updatePatient() {
        val patient = Patient(
            pid = 12,
            firstName = "hello",
            lastName = "world",
            sex = Sex.Male
        ).also { session.save(it) }
        val patientInput = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
            firstName = OptionalInput.Defined("Marcus")
        )

        val result = runBlocking { patientMutation.updatePatient(patientInput, FakeAuth.Valid.Context) }

        assertEquals(result.firstName, patientInput.firstName.getOrNull())
        assertEquals(result.lastName, patient.lastName)
    }

    @Test
    fun addPatient() {
        val patientInput = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
            firstName = OptionalInput.Defined("Marcus"),
            lastName = OptionalInput.Defined("Dunn"),
            sex = OptionalInput.Defined(Sex.Male)
        )

        val result =
            runBlocking { patientMutation.addPatient(patientInput, graphQLAuthContext = FakeAuth.Valid.Context) }

        val expected = GraphQLPatient(
            pid = patientInput.pid.getOrThrow(),
            firstName = patientInput.firstName.getOrThrow(),
            lastName = patientInput.lastName.getOrThrow(),
            sex = patientInput.sex.getOrThrow(),
            authContext = FakeAuth.Valid.Context,
        )

        assertEquals(expected, result)
    }

    @Test
    fun `addPatient persists`() {
        val patientInput = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
            firstName = OptionalInput.Defined("Marcus"),
            lastName = OptionalInput.Defined("Dunn"),
            sex = OptionalInput.Defined(Sex.Male)

        )
        runBlocking { patientMutation.addPatient(patientInput, graphQLAuthContext = FakeAuth.Valid.Context) }

        val result = runBlocking {
            patientQuery.patient(patientInput.pid.getOrThrow(), FakeAuth.Valid.Context)
        }

        val expected = GraphQLPatient(
            pid = patientInput.pid.getOrThrow(),
            firstName = patientInput.firstName.getOrThrow(),
            lastName = patientInput.lastName.getOrThrow(),
            sex = patientInput.sex.getOrThrow(),
            authContext = FakeAuth.Valid.Context,
        )
        assertEquals(expected, result)
    }
}