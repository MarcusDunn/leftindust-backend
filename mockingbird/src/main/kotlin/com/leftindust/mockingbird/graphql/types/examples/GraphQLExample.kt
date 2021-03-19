package com.leftindust.mockingbird.graphql.types.examples

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

fun interface GraphQLExample<T: AbstractJpaPersistable<*>> {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>): List<Predicate>
}