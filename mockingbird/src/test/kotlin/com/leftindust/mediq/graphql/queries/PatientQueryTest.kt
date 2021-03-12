package com.leftindust.mediq.graphql.queries

import com.leftindust.mediq.dao.DoctorDao
import com.leftindust.mediq.dao.VisitDao
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.graphql.types.GraphQLPatient
import com.leftindust.mediq.graphql.types.GraphQLVisit
import com.leftindust.mediq.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mediq.graphql.types.examples.GraphQLPersonExample
import com.leftindust.mediq.graphql.types.examples.StringFilter
import com.leftindust.mediq.graphql.types.input.GraphQLRangeInput
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.DoctorPatientFaker
import com.leftindust.mediq.helper.mocker.PatientFaker
import com.leftindust.mediq.helper.mocker.VisitFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Timestamp
import java.time.Instant
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class PatientQueryTest(
    @Autowired private val patientQuery: PatientQuery,
) {

    @Autowired
    lateinit var sessionFactory: SessionFactory

    private val session
        get() = sessionFactory.currentSession

    private final val fakeAuthContext = FakeAuth.Valid.Context

    @Test
    internal fun `test autowire`() {
        assertDoesNotThrow {
            patientQuery.hashCode()
        }
    }

    @Test
    internal fun `test getPatient return`() {
        val patient = PatientFaker(102).create()
        session.save(patient)

        val result = runBlocking { patientQuery.patient(gqlID(patient.id!!), fakeAuthContext) }

        assertEquals(GraphQLPatient(patient, patient.id!!, fakeAuthContext), result)
    }

    @Test
    internal fun `test getPatients return`() {
        val patientFaker = PatientFaker(102)
        repeat(20) { session.save(patientFaker()) }

        val result = runBlocking {
            patientQuery.patients(
                GraphQLRangeInput(0, 20),
                sortedBy = Patient.SortableField.PID,
                authContext = fakeAuthContext
            )
        }

        assertEquals(20, result.size, "size is different $result")

    }

    @Test
    internal fun `test getPatients subsequent return`() {
        runBlocking {
            val patientFaker = PatientFaker(102)
            repeat(200) { session.save(patientFaker()) }

            val patients = (0 until 5)
                .map { it * 20 }
                .map {
                    patientQuery.patients(
                        GraphQLRangeInput(it, it + 20),
                        sortedBy = Patient.SortableField.PID,
                        authContext = fakeAuthContext
                    )
                }
                .flatten()

            assertEquals(100, patients.size, "comparing size of list $patients")
            assertEquals(
                100,
                patients.map { it.pid }.toSet().size,
                "comparing size of set of pids ${patients.map { it.pid }.toSet().size}"
            )
        }
    }


    @Test
    internal fun `test getPatientsByPids return`() {
        runBlocking {
            val patientFaker = PatientFaker(102)
            val patients = (0..20).map { patientFaker() }.onEach { session.save(it) }
            val pids = patients.map { gqlID(it.id!!) }

            val result = patientQuery.patients(pids = pids, authContext = fakeAuthContext)

            assert(
                result.zip(patients)
                    .all { (graphQLPatient, patient) ->
                        graphQLPatient == GraphQLPatient(
                            patient,
                            patient.id!!,
                            fakeAuthContext
                        )
                    }
            )
        }
    }

    @Test
    internal fun `test getPatientsGrouped return`() {
        runBlocking {
            val patientFaker = PatientFaker(102)
            val patients = (0 until 20)
                .map {
                    patientFaker().apply {
                        firstName = "AAA" // guarantees this will appear first even on a real DB
                    }
                }
                .onEach { session.save(it) }

            val result =
                patientQuery.patientsGrouped(
                    GraphQLRangeInput(0, 20),
                    Patient.SortableField.FIRST_NAME,
                    fakeAuthContext
                ).groups[0]

            result.contents.sortedBy { it.pid.value.toInt() }.zip(patients.sortedBy { it.id!! })
                .forEach { (graphQLPatient, patient) ->
                    assertEquals(
                        graphQLPatient,
                        GraphQLPatient(patient, patient.id!!, fakeAuthContext)
                    )
                }

        }
    }

    @Test
    internal fun `test get doctors from patient`(@Autowired doctorDao: DoctorDao) {
        val doctorPatientFaker = DoctorPatientFaker(102)
        val patient = PatientFaker(102, doctorPatientFaker).create()
        session.save(patient)

        val result = runBlocking { patientQuery.patient(gqlID(patient.id!!), fakeAuthContext) }
        runBlocking {
            assert(GraphQLPatient(patient, patient.id!!, fakeAuthContext).doctors(doctorDao) == result.doctors(doctorDao))
        }
    }

    @Test
    internal fun `test SearchByName`() {
        val doctorPatientFaker = DoctorPatientFaker(102)
        val patient = PatientFaker(102, doctorPatientFaker).create().apply {
            firstName = "TEST Arthur"
            lastName = "Meighen"
        }
        session.save(patient)

        val result = runBlocking { patientQuery.searchPatientsByName("TESTArthur", fakeAuthContext) }

        result
            .zip(listOf(GraphQLPatient(patient, patient.id!!, fakeAuthContext)))
            .forEach { assertEquals(it.first, it.second) }
    }

    @Test
    internal fun `test get visits from patient`(@Autowired visitDao: VisitDao) {
        val patient = PatientFaker(102).create().apply {
            firstName = "William King"
            lastName = "King"
        }
        session.save(patient)

        val visit = VisitFaker(102).create().apply {
            this.patient = patient
        }
        println(visit.doctor)
        session.save(visit.doctor)
        session.saveOrUpdate(visit.patient)
        session.save(visit)

        val result = runBlocking { patientQuery.patient(gqlID(patient.id!!), fakeAuthContext).visits(visitDao) }

        assertEquals(result[0], GraphQLVisit(visit, visit.id!!, fakeAuthContext))
    }


    @Test
    fun patient() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking { patientQuery.patient(gqlID(patient.id!!), FakeAuth.Valid.Context) }

        val expected = GraphQLPatient(patient, patient.id!!, FakeAuth.Valid.Context)
        assertEquals(expected, result)
    }

    @Test
    fun patientsByPids() {
        val patientFaker = PatientFaker(0)
        val patients = (0 until 20).map { patientFaker() }.onEach { session.save(it) }
        val pids = patients.map { gqlID(it.id!!) }

        val result = runBlocking { patientQuery.patients(pids = pids, authContext = FakeAuth.Valid.Context) }

        assertEquals(
            patients.map { GraphQLPatient(it, it.id!!, FakeAuth.Valid.Context) }.sortedBy { it.pid.value },
            result.sortedBy { it.pid.value })
    }

    @Test
    fun patients() {
        val patientFaker = PatientFaker(0)
        val patients = (0 until 20)
            .map { patientFaker() }
            .onEach { session.save(it) }
            .onEach { it.apply { this.firstName = "AAA" } }

        val result = runBlocking {
            patientQuery.patients(
                GraphQLRangeInput(0, 20),
                sortedBy = Patient.SortableField.FIRST_NAME,
                authContext = FakeAuth.Valid.Context
            )
        }


        assertEquals(
            result.sortedBy { it.pid.value },
            patients.map { GraphQLPatient(it, it.id!!, FakeAuth.Valid.Context) }.sortedBy { it.pid.value })
    }

    @Test
    fun patientsGrouped() {
        val patientFaker = PatientFaker(0)
        val patients = (0 until 20)
            .map { patientFaker() }
            .onEach { session.save(it) }
            .onEach { it.apply { this.firstName = "AAA" } }

        val result = runBlocking {
            patientQuery.patientsGrouped(
                GraphQLRangeInput(0, 20),
                Patient.SortableField.FIRST_NAME,
                FakeAuth.Valid.Context
            )
        }

        assertEquals(
            result.groups.flatMap { it.contents }.sortedBy { it.pid.value },
            patients.map { GraphQLPatient(it, it.id!!, FakeAuth.Valid.Context) }.sortedBy { it.pid.value })
    }

    @Test
    @Disabled("does not work due to session issues")
    fun searchPatientsByName() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking { patientQuery.searchPatientsByName("Marcus", FakeAuth.Valid.Context) }

        assert(result.contains(GraphQLPatient(patient, patient.id!!, FakeAuth.Valid.Context)))
    }

    @Test
    @Disabled("does not work due to session issues")
    internal fun `searchPatient by simple example`() {
        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        )
        session.save(patient)

        val result = runBlocking {
            patientQuery.searchPatient(
                GraphQLPatientExample(
                    personalInformation = GraphQLPersonExample(
                        firstName = StringFilter(
                            includes = "arcu",
                        ),
                    )
                ), FakeAuth.Valid.Context
            )
        }

        assert(result.any { it == GraphQLPatient(patient, patient.id!!, FakeAuth.Valid.Context) }) { result }
    }

    @Test
    @Disabled("does not work due to session issues")
    fun `searchPatient by complex example`() {

        val patient = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male
        ).also { session.save(it) }
        val fakeMarcus = Patient(
            firstName = "Darcus",
            lastName = "Munn",
            sex = Sex.Male
        ).also { session.save(it) }
        val imposter = Patient(
            firstName = "Marcus",
            lastName = "Dunn",
            sex = Sex.Male,
            dateOfBirth = Timestamp.from(Instant.ofEpochSecond(1000000))
        ).also { session.save(it) }

        val result = runBlocking {
            patientQuery.searchPatient(
                GraphQLPatientExample(
                    personalInformation = GraphQLPersonExample(
                        firstName = StringFilter(
                            includes = "arcu"
                        )
                    )
                ), FakeAuth.Valid.Context
            )
        }

        assertEquals(
            setOf(patient, fakeMarcus, imposter).map { GraphQLPatient(it, it.id!!, FakeAuth.Valid.Context) }.toSet(),
            result.toSet()
        )
    }
}
