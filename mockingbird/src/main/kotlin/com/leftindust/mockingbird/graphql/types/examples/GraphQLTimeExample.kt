package com.leftindust.mockingbird.graphql.types.examples

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLTimeExample(
    val unixSeconds: LongFilter? = null,
    val unixMilliSeconds: LongFilter? = null,
) {
    fun <T> toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>, field: String): List<Predicate> {
        return listOfNotNull(
            unixSeconds?.toPredicate(criteriaBuilder, itemRoot, field),
            unixMilliSeconds?.toPredicate(criteriaBuilder, itemRoot, field),
        ).flatten()
    }
}