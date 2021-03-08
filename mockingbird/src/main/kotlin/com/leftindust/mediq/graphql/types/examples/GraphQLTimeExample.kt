package com.leftindust.mediq.graphql.types.examples

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLTimeExample(
    val unixSeconds: LongFilter? = null,
    val unixMilliSeconds: LongFilter? = null,
) {
    fun <T> toPredicate(criteriaQuery: CriteriaBuilder, itemRoot: Root<T>, field: String): List<Predicate> {
        TODO("Not yet implemented")
    }
}