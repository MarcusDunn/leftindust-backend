package com.leftindust.mediq.graphql.types

import com.leftindust.mediq.dao.PatientDao
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.graphql.types.examples.GraphQLPatientExample
import com.leftindust.mediq.graphql.types.examples.GraphQLPersonExample
import com.leftindust.mediq.graphql.types.examples.StringFilter
import com.leftindust.mediq.helper.FakeAuth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import unwrap

@SpringBootTest
internal class GraphQLPatientExampleTest {

    @Test
    fun toSqlConditions(@Autowired patientDao: PatientDao) {
        runBlocking {

            val patient = Patient(
                pid = 11,
                firstName = "Marcus",
                lastName = "Dunn",
                sex = Sex.Male
            ).also { patientDao.addNewPatient(it, FakeAuth.Valid.Token) }
            patientDao.addNewPatient(patient, FakeAuth.Valid.Token)
            val example = GraphQLPatientExample(
                personalInformation = GraphQLPersonExample(
                    firstName = StringFilter(
                        eq = "Marcus"
                    ),
                    lastName = StringFilter(
                        includes = "Dun",
                    ),
                )
            )

            val result = patientDao.searchByExample(example, FakeAuth.Valid.Token)

            assertEquals(listOf(patient), result.unwrap())

            patientDao.removePatientByPID(patient.pid, FakeAuth.Valid.Token)
        }
    }

    @Test
    fun `toSqlConditions no conditions`(@Autowired patientDao: PatientDao) {
        runBlocking {
            val patient = Patient(
                pid = 11,
                firstName = "Marcus",
                lastName = "Dunn",
                sex = Sex.Male
            ).also { patientDao.addNewPatient(it, FakeAuth.Valid.Token) }
            val example = GraphQLPatientExample()

            val result = patientDao.searchByExample(example, FakeAuth.Valid.Token)

            assert(result.unwrap().contains(patient)) { result.unwrap() }

            patientDao.removePatientByPID(patient.pid, FakeAuth.Valid.Token)
        }
    }

}
