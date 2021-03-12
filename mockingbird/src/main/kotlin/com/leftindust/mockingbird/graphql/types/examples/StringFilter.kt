package com.leftindust.mockingbird.graphql.types.examples

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class StringFilter(
    val eq: String? = null,
    val ne: String? = null,
    val includes: String? = null,
    val startsWith: String? = null,
    val endWith: String? = null,
) {
    fun <T> toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>, field: String): List<Predicate> {
        return listOfNotNull(
            eq?.let { criteriaBuilder.equal(itemRoot.get<String>(field), eq) },
            ne?.let { criteriaBuilder.notEqual(itemRoot.get<String>(field), ne) },
            includes?.let { criteriaBuilder.like(itemRoot.get(field), "%$includes%") },
            startsWith?.let { criteriaBuilder.like(itemRoot.get(field), "$startsWith%") },
            endWith?.let { criteriaBuilder.like(itemRoot.get(field), "%$endWith") },
        )
    }
}