package com.leftindust.mockingbird.graphql.types.example

import com.leftindust.mockingbird.dao.entity.NameInfo
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.metamodel.SingularAttribute

internal class StringFilerTest {

    @Test
    fun toPredicate() {
        val stringFiler = StringFiler(eq = "whoa")
        val mockkRoot = mockk<Root<NameInfo>>()
        val mockkCriteriaBuilder = mockk<CriteriaBuilder>()
        val mockkColumnName = mockk<SingularAttribute<NameInfo, String>>()
        val mockkPath = mockk<Path<String>>()
        val mockkPredicate = mockk<Predicate>()
        val mockkCombinedPredicate = mockk<Predicate>()

        every { mockkRoot.get(mockkColumnName) } returns mockkPath
        every { mockkCriteriaBuilder.equal(mockkPath, stringFiler.eq) } returns mockkPredicate
        every { mockkCriteriaBuilder.and(mockkPredicate) } returns mockkCombinedPredicate

        val result = stringFiler.toPredicate(mockkCriteriaBuilder, mockkRoot, mockkColumnName)

        verifyAll {
            mockkCriteriaBuilder.equal(mockkPath, stringFiler.eq)
            mockkCriteriaBuilder.and(mockkPredicate)
            mockkRoot.get(mockkColumnName)
        }

        confirmVerified(
            mockkRoot,
            mockkCriteriaBuilder,
            mockkColumnName,
            mockkPath,
            mockkPredicate,
            mockkCombinedPredicate
        )

        assertEquals(mockkCombinedPredicate, result)
    }
}