package com.leftindust.mediq.graphql.mutations

import com.leftindust.mediq.dao.entity.Doctor
import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.enums.Sex
import com.leftindust.mediq.extensions.gqlID
import com.leftindust.mediq.graphql.types.GraphQLTime
import com.leftindust.mediq.graphql.types.GraphQLVisit
import com.leftindust.mediq.graphql.types.GraphQLVisitInput
import com.leftindust.mediq.graphql.types.icd.FoundationIcdCode
import com.leftindust.mediq.helper.FakeAuth
import com.leftindust.mediq.helper.mocker.TimestampFaker
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
            pid = 12,
            firstName = "marcus",
            lastName = "dunn",
            sex = Sex.Male,
        ).also { session.save(it) }
        val doctor = Doctor(
            did = 12,
            firstName = "marcus",
            lastName = "dunn",
        ).also { session.save(it) }
        val timeBooked = GraphQLTime(TimestampFaker(100).create())
        val timeOfVisit = GraphQLTime(TimestampFaker(101).create())
        val foundationIcdCode = FoundationIcdCode("2123412")
        val visit = GraphQLVisitInput(
            timeBooked = timeBooked,
            timeOfVisit = timeOfVisit,
            foundationIcdCode = foundationIcdCode,
            doctorId = gqlID(patient.pid),
            patientId = gqlID(doctor.did),
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