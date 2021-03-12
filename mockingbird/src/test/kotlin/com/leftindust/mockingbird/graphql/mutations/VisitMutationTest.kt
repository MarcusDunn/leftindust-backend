package com.leftindust.mockingbird.graphql.mutations

import com.leftindust.mockingbird.dao.entity.Doctor
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.enums.Sex
import com.leftindust.mockingbird.extensions.gqlID
import com.leftindust.mockingbird.graphql.types.GraphQLTime
import com.leftindust.mockingbird.graphql.types.GraphQLVisit
import com.leftindust.mockingbird.graphql.types.GraphQLVisitInput
import com.leftindust.mockingbird.graphql.types.icd.FoundationIcdCode
import com.leftindust.mockingbird.helper.FakeAuth
import com.leftindust.mockingbird.helper.mocker.TimestampFaker
import kotlinx.coroutines.runBlocking
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class VisitMutationTest(
    @Autowired private val visitMutation: VisitMutation
) {

    @Autowired
    lateinit var sessionFactory: SessionFactory

    private val session: Session
        get() = sessionFactory.currentSession


    @Test
    fun addVisit() {
        val patient = Patient(
            firstName = "marcus",
            lastName = "dunn",
            sex = Sex.Male,
        )
        val patientID = session.save(patient) as Long

        val doctor = Doctor(
            firstName = "marcus",
            lastName = "dunn",
        )
        val doctorId = session.save(doctor) as Long

        val timeBooked = GraphQLTime(TimestampFaker(100).create())
        val timeOfVisit = GraphQLTime(TimestampFaker(101).create())
        val foundationIcdCode = FoundationIcdCode("2123412")
        val visit = GraphQLVisitInput(
            timeBooked = timeBooked,
            timeOfVisit = timeOfVisit,
            foundationIcdCode = foundationIcdCode,
            doctorId = gqlID(doctorId),
            patientId = gqlID(patientID),
        )
        val result = runBlocking { visitMutation.addVisit(visit, FakeAuth.Valid.Context) }
        val expected = GraphQLVisit(
            vid = result.vid,
            timeBooked = timeBooked,
            timeOfVisit = timeOfVisit,
            foundationIcdCode = foundationIcdCode,
            authContext = FakeAuth.Valid.Context,
        )

        assertEquals(expected, result)
    }
}