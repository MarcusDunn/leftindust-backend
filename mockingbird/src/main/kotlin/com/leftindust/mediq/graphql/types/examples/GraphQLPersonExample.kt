package com.leftindust.mediq.graphql.types.examples

import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.superclasses.Person_
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLPersonExample(
    val firstName: StringFilter? = null,
    val middleName: StringFilter? = null,
    val lastName: StringFilter? = null,
    val phoneNumbers: List<GraphQLPhoneNumberExample>? = null
) {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>): List<Predicate> {
        if (phoneNumbers != null) TODO()
        return listOfNotNull(
            firstName?.toPredicate(criteriaBuilder, itemRoot, Person_.FIRST_NAME),
            middleName?.toPredicate(criteriaBuilder, itemRoot, Person_.MIDDLE_NAME),
            lastName?.toPredicate(criteriaBuilder, itemRoot, Person_.LAST_NAME),
        ).flatten()
    }
}