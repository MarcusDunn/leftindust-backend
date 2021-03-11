package com.leftindust.mediq.dao

import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.entity.DoctorPatient
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.Visit
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.getOrNull
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.extensions.toInt
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.graphql.types.input.GraphQLPatientInput
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.PatientFaker
import com.leftindust.mediq.helper.mocker.TimestampFaker
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
        session.save(patient)

        val result = runBlocking { patientDao.getByPID(patient.pid, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())

    }

    @Test
    fun `getByPID with non-existent Pid`() {
        runBlocking {
            val result = runBlocking { patientDao.getByPID(0, FakeAuth.Valid.Token) }
            assert(result.unwrapFailure() is DoesNotExist)

        }
    }

    @Test
    fun getManyGroupedBySorted() {
        val patientFaker = PatientFaker(0)
        val patients = (0 until 20)
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

        assertEquals(patients.sortedBy { it.pid }, result.unwrap().flatMap { it.value }.sortedBy { it.pid })

    }

    @Test
    fun addNewPatientByPID() {
        val patient = Patient(
            pid = 0,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Female,
        )

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token).unwrap() }

        assertEquals(session.get(Patient::class.java, result.id).pid, patient.pid)

    }

    @Test
    fun `addNewPatientByPID with already existing patient`() {
        val patient = Patient(
            pid = 0,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Intersex,
        )
        session.save(patient)

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is AlreadyExists)
    }

    @Test
    fun removePatientByPID() {
        val patient = Patient(
            pid = 0,
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking { patientDao.removePatientByPID(patient.pid, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())

        val expected = runBlocking { patientDao.getByPID(patient.pid, FakeAuth.Valid.Token) }
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
            pid = 0,
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
            pid = 0,
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
            pid = 0,
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
        val vistId = session.save(visit) as Long

        val result = runBlocking { patientDao.getByVisit(vistId, FakeAuth.Valid.Token) }

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
            pid = 12,
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
            pid = 12,
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male,
        )

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token) }

        assertEquals(patient, result.unwrap())
    }

    @Test
    internal fun `addPatient with duplicate pid`() {
        val patient = Patient(
            pid = 12,
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male,
        ).also { session.save(it) }

        val result = runBlocking { patientDao.addNewPatient(patient, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is AlreadyExists)
    }

    @Test
    internal fun `addPatient by GraphQLInput`() {
        val doctor = Doctor(
            firstName = "Elongated",
            lastName = "Muskrat",
        )
        val doctorID = session.save(doctor) as Long

        val gqlPatient = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("100")),
            firstName = OptionalInput.Defined("Marky"),
            lastName = OptionalInput.Defined("Moo"),
            sex = OptionalInput.Defined(Sex.Male)
        )

        val result = runBlocking {
            patientDao.addNewPatient(gqlPatient, doctorIds = listOf(gqlID(doctorID)), requester = FakeAuth.Valid.Token)
        }

        assertEquals(result.unwrap().pid, gqlPatient.pid.getOrNull()!!.toInt())
        assertEquals(result.unwrap().firstName, gqlPatient.firstName.getOrNull())
        assertEquals(result.unwrap().lastName, gqlPatient.lastName.getOrNull())
        assertEquals(result.unwrap().doctors.first().doctor.id, doctorID)
    }

    @Test
    internal fun `addPatient with invalid GraphQLInput`() {
        Doctor(
            firstName = "Elongated",
            lastName = "Muskrat",
        ).let { session.save(it) }

        val gqlPatient = GraphQLPatientInput(
            firstName = OptionalInput.Defined("Marky"),
            lastName = OptionalInput.Defined("Moo"),
        )

        val result = runBlocking {
            patientDao.addNewPatient(
                patient = gqlPatient,
                doctorIds = listOf(ID("12")),
                requester = FakeAuth.Valid.Token
            )
        }

        assert(result.unwrapFailure() is InvalidArguments)
    }

    @Test
    internal fun setPatient() {
        val patient = Patient(
            pid = 12,
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        ).also { session.save(it) }
        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
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
                pid = 12,
                firstName = "Steve",
                lastName = "O",
                sex = Sex.Male
            ).also { session.save(it) }
            val update = GraphQLPatientInput(
                firstName = OptionalInput.Defined("marcus")
            )
            patientDao.update(update, FakeAuth.Valid.Token)

            val result = patientDao.getByPID(12, FakeAuth.Valid.Token)

            val expected = patient.apply { firstName = "marcus" }
            assertEquals(expected, result.unwrap())
        }

    }

    @Test
    internal fun `setPatient with invalid patient`() {
        Patient(
            pid = 12,
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        ).also { session.save(it) }
        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
            firstName = OptionalInput.Defined("marcus"),
            lastName = OptionalInput.Defined(null)
        )
        val result = runBlocking { patientDao.update(update, FakeAuth.Valid.Token) }

        assert(result.unwrapFailure() is InvalidArguments)
    }

    @Test
    internal fun `setPatient doesnt change values when undefined`() {
        val patient = Patient(
            pid = 12,
            firstName = "Steve",
            lastName = "O",
            sex = Sex.Male
        ).also { session.save(it) }
        val update = GraphQLPatientInput(
            pid = OptionalInput.Defined(ID("12")),
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
                pid = 12,
                firstName = "Steve",
                lastName = "O",
                sex = Sex.Male
            ).also { session.save(it) }
            val update = GraphQLPatientInput(
                pid = OptionalInput.Defined(ID("12")),
                firstName = OptionalInput.Defined("marcus"),
                lastName = OptionalInput.Undefined
            )
            patientDao.update(update, FakeAuth.Valid.Token)

            val result = patientDao.getByPID(12, FakeAuth.Valid.Token)

            assertEquals(result.unwrap().lastName, patient.lastName)
        }

    }
}