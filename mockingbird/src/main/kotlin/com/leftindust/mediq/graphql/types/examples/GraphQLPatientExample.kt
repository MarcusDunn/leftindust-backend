package com.leftindust.mediq.graphql.types.examples

import com.leftindust.mediq.dao.entity.Patient
import com.leftindust.mediq.dao.entity.Patient_
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLPatientExample(
    val personalInformation: GraphQLPersonExample? = null,
    val pid: StringFilter? = null,
    val dateOfBirth: GraphQLTimeExample? = null,
    val address: StringFilter? = null,
    val email: StringFilter? = null,
    val insuranceNumber: StringFilter? = null,
    val contacts: List<GraphQLEmergencyContactExample>? = null,
    val doctors: List<GraphQLDoctorExample>? = null,
    val visits: List<GraphQLVisitExample>? = null
) {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>): List<Predicate> {
        if (contacts != null) TODO()
        if (doctors != null) TODO()
        if (visits != null) TODO()
        return listOfNotNull(
            personalInformation?.toPredicate(criteriaBuilder, itemRoot),
            pid?.toPredicate(criteriaBuilder, itemRoot, Patient_.PID),
            dateOfBirth?.toPredicate(criteriaBuilder, itemRoot, Patient_.DATE_OF_BIRTH),
            address?.toPredicate(criteriaBuilder, itemRoot, Patient_.ADDRESS),
            email?.toPredicate(criteriaBuilder, itemRoot, Patient_.EMAIL),
            insuranceNumber?.toPredicate(criteriaBuilder, itemRoot, Patient_.INSURANCE_NUMBER),
        ).flatten()
    }
}