package com.leftindust.mediq.graphql.types.examples

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class LongFilter(
    val eq: Long? = null,
    val ne: Long? = null,
    val gt: Long? = null,
    val lt: Long? = null,
) {
    fun <T> toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>, field: String): List<Predicate> {
        return listOfNotNull(
            eq?.let { criteriaBuilder.equal(itemRoot.get<Long>(field), eq) },
            ne?.let { criteriaBuilder.notEqual(itemRoot.get<Long>(field), ne) },
            gt?.let { criteriaBuilder.greaterThan(itemRoot.get<Long>(field), gt) },
            lt?.let { criteriaBuilder.lessThan(itemRoot.get<Long>(field), lt) },
        )
    }
}