package com.leftindust.mockingbird.graphql.types.example

import com.leftindust.mockingbird.dao.entity.NameInfo
import com.leftindust.mockingbird.dao.entity.NameInfo_
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Join
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

internal class GraphQLPatientExampleTest {

    @Test
    fun asCriteria() {
        val mockkFirstName = mockk<StringFiler>()
        val mockkLastName = mockk<StringFiler>()
        val patientEx = GraphQLPatientExample(
            firstName = mockkFirstName,
            lastName = mockkLastName,
        )

        val mockkCB = mockk<CriteriaBuilder>()
        val mockkRoot = mockk<Root<Patient>>()
        val mockkJoin = mockk<Join<Patient, NameInfo>>()
        val mockkFNPred = mockk<Predicate>()
        val mockkLNPred = mockk<Predicate>()
        val combinedPredicate = mockk<Predicate>()

        every { mockkRoot.join(Patient_.nameInfo) } returns mockkJoin
        every { mockkFirstName.toPredicate(mockkCB, mockkJoin, NameInfo_.firstName) } returns mockkLNPred
        every { mockkLastName.toPredicate(mockkCB, mockkJoin, NameInfo_.lastName) } returns mockkFNPred
        every { mockkCB.and(mockkLNPred, mockkFNPred) } returns combinedPredicate

        val result = patientEx.toPredicate(
            mockkCB,
            mockkRoot,
        )

        verifyAll {
            mockkRoot.join(Patient_.nameInfo)
            mockkCB.and(mockkLNPred, mockkFNPred)
            mockkFirstName.toPredicate(mockkCB, mockkJoin, NameInfo_.firstName)
            mockkLastName.toPredicate(mockkCB, mockkJoin, NameInfo_.firstName)
        }

        confirmVerified(
            mockkCB,
            mockkRoot,
            mockkFirstName,
            mockkFNPred,
            mockkJoin,
            mockkLastName,
            mockkLNPred
        )

        assertEquals(combinedPredicate, result)
    }
}