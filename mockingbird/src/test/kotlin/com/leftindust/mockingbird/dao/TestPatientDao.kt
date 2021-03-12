package com.leftindust.mockingbird.dao

import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.PatientFaker
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import unwrap
import unwrapFailure

@SpringBootTest
@Transactional
class TestPatientDao {

    @Autowired
    lateinit var patientDao: PatientDao

    val fakeAuthToken = FakeAuth.Valid.Token

    @Test
    internal fun `test addPatient with valid patient`() {
        val patient = Patient(
            firstName = "John",
            middleName = "Alexander",
            lastName = "Macdonald",
            sex = Sex.Male
        )

        val result = runBlocking { patientDao.addNewPatient(patient, fakeAuthToken) }

        assert(result.isSuccess()) { "failed to add a patient to the patientDao" }
    }

    @Test
    internal fun `test getPatient with valid patient`() {
        runBlocking {
            val patient = Patient(
                firstName = "Alexander",
                lastName = "Mackenzie",
                sex = Sex.Male
            )
            val patientID = patientDao.addNewPatient(patient, fakeAuthToken)
                .let { it.unwrap().id!! }

            val result = patientDao.getByPID(patientID, fakeAuthToken)

            assert(result.isSuccess()) { "failed to get a patient from the patientDao" }
        }

    }

    @Test
    internal fun `test getPatient return value with valid patient`() {
        val patient = Patient(
            firstName = "Alexander",
            lastName = "Mackenzie",
            sex = Sex.Male
        )
        val patientID = runBlocking { patientDao.addNewPatient(patient, fakeAuthToken) }.let { it.unwrap().id!! }

        val result = runBlocking { patientDao.getByPID(patientID, fakeAuthToken).unwrap() }

        assertEquals(result, patient)

    }

    @Test
    internal fun `test getPatient with no patient returns DoesNotExist`() {
        val result = runBlocking { patientDao.getByPID(1, fakeAuthToken).unwrapFailure() }
        assert(result is DoesNotExist)
    }

    @Test
    internal fun `test removePatient with invalid patient returns DoesNotExist`() {
        val result = runBlocking { patientDao.removePatientByPID(7, fakeAuthToken).unwrapFailure() }
        assert(result is DoesNotExist)
    }

    @Test
    internal fun `test removePatient returns removed patient`() {
        runBlocking {
            val patient = Patient(
                firstName = "Robert",
                lastName = "Borden",
                sex = Sex.Male
            )

            val patientID = patientDao.addNewPatient(patient, fakeAuthToken).let { it.unwrap().id!! }

            val result = patientDao.removePatientByPID(patientID, fakeAuthToken).unwrap()

            assertEquals(result, patient)
        }

    }

    @Test
    internal fun `test removePatient changes result of getPatient`() {
        runBlocking {
            val patient = Patient(
                firstName = "Arthur",
                lastName = "Meighen",
                sex = Sex.Male
            )
            val patientPID = patientDao.addNewPatient(patient, fakeAuthToken).let { it.unwrap().id!! }
            patientDao.removePatientByPID(patientPID, fakeAuthToken)

            val result = runBlocking { patientDao.getByPID(patientPID, fakeAuthToken).unwrapFailure() }

            assert(result is DoesNotExist)
        }
    }

    @Test
    internal fun `test getManyPatientsGrouped return value`() {
        runBlocking {
            val patientFaker =
                PatientFaker(101) // can be literally any seed but 100 as that is reserved for populating the database
            repeat(20) {
                val added = patientFaker()
                patientDao.addNewPatient(added, fakeAuthToken)
            }

            val result =
                patientDao.getManyGroupedBySorted(0, 20, Patient.SortableField.PID, fakeAuthToken).unwrap()
            assertEquals(20, result.values.flatten().size)
        }
    }
}