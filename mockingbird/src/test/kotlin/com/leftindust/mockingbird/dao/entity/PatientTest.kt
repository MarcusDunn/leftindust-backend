package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.execution.OptionalInput
import com.expediagroup.graphql.scalars.ID
import com.leftindust.mockingbird.dao.entity.enums.Ethnicity
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneNumber
import com.leftindust.mockingbird.graphql.types.GraphQLPhoneType
import com.leftindust.mockingbird.graphql.types.GraphQLTime
import com.leftindust.mockingbird.graphql.types.input.GraphQLPatientInput
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.sql.Timestamp

internal class PatientTest {

    @Test
    fun addDoctor() {
        val patient = Patient(
            firstName = "marcus",
            lastName = "dunn",
            sex = Sex.Male,
        )

        val doctor = spyk<Doctor>() {
            patients = emptySet()
        }

        patient.addDoctor(doctor)

        assertEquals(doctor, patient.doctors.first().doctor)
        assertEquals(patient, doctor.patients.first().patient)
    }

    @Test
    internal fun `create by GraphQLPatientInput`() {
        val graphQLPatientInput = GraphQLPatientInput(
            firstName = OptionalInput.Defined("marcus"),
            middleName = OptionalInput.Defined("elliot"),
            lastName = OptionalInput.Defined("dunn"),
            phoneNumbers = OptionalInput.Defined(
                listOf(
                    GraphQLPhoneNumber(
                        number = 7789913091,
                        type = GraphQLPhoneType.Home
                    )
                )
            ),
            dateOfBirth = OptionalInput.Defined(GraphQLTime(Timestamp.valueOf("2020-01-02 09:01:15"))),
            address = OptionalInput.Defined("874 West 1st street"),
            emails = OptionalInput.Defined(listOf("hello@world.ca")),
            insuranceNumber = OptionalInput.Defined(ID("129112")),
            sex = OptionalInput.Defined(Sex.Male),
            gender = OptionalInput.Defined(Sex.Male.toString()),
            ethnicity = OptionalInput.Defined(Ethnicity.White),
            doctors = OptionalInput.Defined(listOf(gqlID(23), gqlID(55))),
        )
        val mockkSession = mockk<Session>() {
            every { get(Doctor::class.java, 23L) } returns mockk() {
                every { addPatient(any()) } returns mockk()
            }
            every { get(Doctor::class.java, 55L) } returns mockk() {
                every { addPatient(any()) } returns mockk()
            }
        }
        val result = Patient(graphQLPatientInput, mockkSession)
        println(result)
    }
}