package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLPatientExample(
    val personalInformation: GraphQLPersonExample? = null,
    val pid: StringFilter? = null,
    val dateOfBirth: GraphQLTimeExample? = null,
    val address: StringFilter? = null,
    val emails: StringListFilter? = null,
    val insuranceNumber: StringFilter? = null,
) : @GraphQLIgnore GraphQLExample<Patient> {

    override fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>): List<Predicate> {
        return listOfNotNull(
            personalInformation?.toPredicate(criteriaBuilder, itemRoot),
            pid?.toPredicate(criteriaBuilder, itemRoot, Patient_.ID),
            dateOfBirth?.toPredicate(criteriaBuilder, itemRoot, Patient_.DATE_OF_BIRTH),
            address?.toPredicate(criteriaBuilder, itemRoot, Patient_.ADDRESS),
            emails?.toPredicate(criteriaBuilder, itemRoot, Patient_.EMAILS),
            insuranceNumber?.toPredicate(criteriaBuilder, itemRoot, Patient_.INSURANCE_NUMBER),
        ).flatten()
    }
}

class StringListFilter(
    val includes: String? = null,
) {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>, field: String): List<Predicate> {
        return listOfNotNull(
            includes?.let { criteriaBuilder.isMember(it, itemRoot.get(field)) }
        )
    }
}