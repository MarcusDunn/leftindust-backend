package com.leftindust.mockingbird.dao

import com.expediagroup.graphql.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.DoctorPatient
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.getOrThrow
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLPatient
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.PatientFaker
import com.leftindust.mockingbird.helper.mocker.TimestampFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@Transactional
@SpringBootTest
internal class PatientDaoTest(
    @Autowired private val patientDao: PatientDao
) {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    @Test
    fun getByPID() {
        val patient = PatientFaker(0).create()
        val patientID = session.save(patient) as Long

        val result = runBlocking { patientDao.getByPID(patientID, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())

    }

    @Test
    fun `getByPID with non-existent Pid`() {
        runBlocking {
            val result = runBlocking { patientDao.getByPID(-1, FakeAuth.Valid.Token) }
            assert(result.unwrapFailure() is DoesNotExist)

        }
    }

    @Test
    fun getManyGroupedBySorted() {
        val patientFaker = PatientFaker(0)
        val patients = (0 until 20)
            // make sure they appear first when sorted by first name
            .map { patientFaker().apply { this.firstName = "AAA" } }
            .onEach { session.save(it) }

        val result =
            runBlocking {
                patientDao.getManyGroupedBySorted(
                    0,
                    20,
                    Patient.SortableField.FIRST_NAME,
                    FakeAuth.Valid.Token
                )
            }

        assertEquals(patients.sortedBy { it.id }.toSet(), result.unwrap().flatMap { it.value }.toSet())

    }

    @Test
    fun addNewPatientByPID() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Female,
        )

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token).unwrap() }

        assertEquals(session.get(Patient::class.java, result.id), patient)

    }

    @Test
    fun removePatientByPID() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long

        val result = runBlocking { patientDao.removePatientByPID(patientID, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())

        val expected = runBlocking { patientDao.getByPID(patientID, FakeAuth.Valid.Token) }
        assert(expected.unwrapFailure() is DoesNotExist)

    }

    @Test
    fun `removePatientByPID with no such patient`() {
        val result = runBlocking { patientDao.removePatientByPID(0, FakeAuth.Valid.Token) }
        assert(result.unwrapFailure() is DoesNotExist)
    }

    @Test
    fun searchByName() {
        val patient = Patient(
            firstName = "Dan",
            lastName = "Shervershani",
            sex = Sex.Intersex,
        )
        session.save(patient)

        val result = runBlocking { patientDao.searchByName(patient.lastName, FakeAuth.Valid.Token) }

        assertEquals(listOf(patient), result.unwrap())
    }

    @Test
    fun `searchByName with no such name`() {
        val result = runBlocking { patientDao.searchByName("Shervershani", FakeAuth.Valid.Token) }
        assertEquals(emptyList<Patient>(), result.unwrap())
    }

    @Test
    fun `searchByName empty string`() {
        val result = runBlocking { patientDao.searchByName("", FakeAuth.Valid.Token) }
        assert(result.unwrapFailure() is InvalidArguments)
    }

    @Test
    fun getByDoctor() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Female
        )
        session.save(patient)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Shervershani",
        )
        val doctorID = session.save(doctor) as Long
        val doctorPatient = DoctorPatient(
            doctor = doctor,
            patient = patient,
        )
        session.save(doctorPatient)
        patient.doctors = setOf(doctorPatient)
        doctor.patients = setOf(doctorPatient)

        val result = runBlocking { patientDao.getByDoctor(doctorID, FakeAuth.Valid.Token) }

        assertEquals(result.unwrap(), listOf(patient))
    }

    @Test
    fun `getByDoctor when no such doctor exists`() {
        val result = runBlocking { patientDao.getByDoctor(-1, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }

    @Test
    fun getByVisit() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Female
        )
        session.save(patient)
        val doctor = Doctor(
            firstName = "Dan",
            lastName = "Shervershani",
        )
        session.save(doctor)
        val visit = Visit(
            doctor = doctor,
            patient = patient,
            timeOfVisit = TimestampFaker(0).create(),
            timeBooked = TimestampFaker(1).create(),
            icdFoundationCode = FoundationIcdCode("1"),
        )
        val visitId = session.save(visit) as Long

        val result = runBlocking { patientDao.getByVisit(visitId, FakeAuth.Valid.Token) }

        assertEquals(result.unwrap(), patient)
    }

    @Test
    fun `getByVisit when no such visit exists`() {
        val result = runBlocking { patientDao.getByVisit(-1, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is DoesNotExist)
    }

    @Test
    fun addDoctorToPatient() {
        val doctor = Doctor(
            firstName = "marcus",
            lastName = "dunn",
        )
        val doctorID = session.save(doctor) as Long
        val patient = Patient(
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male,
        )
        val patientID = session.save(patient) as Long

        val result = runBlocking {
            patientDao.addDoctorToPatient(
                patientInput = gqlID(patientID),
                doctorInput = gqlID(doctorID),
                FakeAuth.Valid.Token
            )
        }

        val expected = patient.apply {
            doctors.toMutableSet().apply {
                add(DoctorPatient(patient, doctor))
            }
        }

        assertEquals(expected, result.unwrap())
    }

    @Test
    internal fun addPatient() {
        val patient = Patient(
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male,
        )

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())
    }

    @Test
    internal fun `addPatient by GraphQLInput`() {
        val doctor = Doctor(
            firstName = "Elongated",
            lastName = "Muskrat",
        )
        val doctorID = session.save(doctor) as Long

        val gqlPatient = GraphQLPatientInput(
            firstName = OptionalInput.Defined("Marky"),
            lastName = OptionalInput.Defined("Moo"),
            sex = OptionalInput.Defined(Sex.Male)
        )

        val result = runBlocking {
            patientDao.addNewPatient(gqlPatient, doctorIds = listOf(gqlID(doctorID)), requester = FakeAuth.Valid.Token)
        }.unwrap()

        val expected = GraphQLPatient(
            pid = gqlID(result.id!!),
            firstName = gqlPatient.firstName.getOrThrow(),
            lastName = gqlPatient.lastName.getOrThrow(),
            sex = gqlPatient.sex.getOrThrow(),
            authContext = FakeAuth.Valid.Context,
        )
        assertEquals(expected, GraphQLPatient(result, result.id!!, FakeAuth.Valid.Context))
    }

    @Test
    internal fun `addPatient with invalid GraphQLInput`() {
        val doctor = Doctor(
            firstName = "Elongated",
            lastName = "Muskrat",
        )
        val doctorID = session.save(doctor) as Long

        val gqlPatient = GraphQLPatientInput(
            firstName = OptionalInput.Defined("Marky"),
            lastName = OptionalInput.Defined("Moo"),
        )

        val result = runBlocking {
            patientDao.addNewPatient(
                patient = gqlPatient,
                doctorIds = listOf(gqlID(doctorID)),
                requester = FakeAuth.Valid.Token
            )
        }

        assert(result.unwrapFailure() is InvalidArguments)
    }

    @Test
    internal fun setPatient() {
        val patient = Patient(
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long

        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(gqlID(patientID)),
            firstName = OptionalInput.Defined("marcus"),
        )


        val result = runBlocking { patientDao.update(update, FakeAuth.Valid.Token) }

        val expected = patient.apply { firstName = "marcus" }
        assertEquals(expected, result.unwrap())
    }

    @Test
    internal fun `setPatient persists`() {
        runBlocking {
            val patient = Patient(
                firstName = "Steve",
                lastName = "O",
                sex = Sex.Male
            )
            val patientID = session.save(patient) as Long
            val update = GraphQLPatientInput(
                pid = OptionalInput.Defined(gqlID(patientID)),
                firstName = OptionalInput.Defined("marcus"),
            )
            patientDao.update(update, FakeAuth.Valid.Token)

            val result = patientDao.getByPID(patientID, FakeAuth.Valid.Token)

            val expected = patient.apply { firstName = "marcus" }
            assertEquals(expected, result.unwrap())
        }

    }

    @Test
    internal fun `setPatient with invalid patient`() {
        val patient = Patient(
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long
        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(gqlID(patientID)),
            firstName = OptionalInput.Defined("marcus"),
            lastName = OptionalInput.Defined(null)
        )
        val result = runBlocking { patientDao.update(update, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is InvalidArguments)
    }

    @Test
    internal fun `setPatient doesnt change values when undefined`() {
        val patient = Patient(
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        )
        val patientID = session.save(patient) as Long

        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(gqlID(patientID)),
            firstName = OptionalInput.Defined("marcus"),
            lastName = OptionalInput.Undefined
        )

        val result = runBlocking { patientDao.update(update, FakeAuth.Valid.Token) }

        assertEquals(result.unwrap().lastName, patient.lastName)
    }

    @Test
    internal fun `setPatient doesnt change values when undefined persists`() {
        runBlocking {
            val patient = Patient(
                firstName = "Steve",
                lastName = "O",
                sex = Sex.Male
            )
            val patientID = session.save(patient) as Long
            val update = GraphQLPatientInput(
                pid = OptionalInput.Defined(gqlID(patientID)),
                firstName = OptionalInput.Defined("marcus"),
                lastName = OptionalInput.Undefined
            )
            patientDao.update(update, FakeAuth.Valid.Token)

            val result = patientDao.getByPID(patientID, FakeAuth.Valid.Token)

            assertEquals(result.unwrap().lastName, patient.lastName)
        }

    }
}