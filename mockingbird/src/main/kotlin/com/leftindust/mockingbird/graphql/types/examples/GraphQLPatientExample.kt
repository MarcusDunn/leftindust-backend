package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.leftindust.mockingbird.dao.entity.Email_
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLPatientExample(
    val personalInformation: GraphQLPersonExample? = null,
    val pid: StringFilter? = null,
    val dateOfBirth: GraphQLTimeExample? = null,
    val address: StringFilter? = null,
    val emails: EmailListFilter? = null,
    val insuranceNumber: StringFilter? = null,
) : @GraphQLIgnore GraphQLExample<Patient> {

    override fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>): List<Predicate> {
        if (emails != null) TODO()
        if (address != null) TODO()
        return listOfNotNull(
            personalInformation?.toPredicate(criteriaBuilder, itemRoot),
            pid?.toPredicate(criteriaBuilder, itemRoot, Patient_.ID),
            dateOfBirth?.toPredicate(criteriaBuilder, itemRoot, Patient_.DATE_OF_BIRTH),
            insuranceNumber?.toPredicate(criteriaBuilder, itemRoot, Patient_.INSURANCE_NUMBER),
        ).flatten()
    }
}

data class GraphQLTimeExample(
    val beforeUnixMilli: Long,
    val afterUnixMilli: Long,
) {
    fun <T> toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>, feild: String): List<Predicate> {
        return listOfNotNull(
            criteriaBuilder.between(
                itemRoot.get(feild),
                Timestamp.from(Instant.ofEpochMilli(afterUnixMilli)),
                Timestamp.from(Instant.ofEpochMilli(beforeUnixMilli)),
            )
        )
    }
}

data class EmailListFilter(
    val includes: EmailExample? = null
) {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Patient>, field: String): List<Predicate> {
        return listOfNotNull(
            includes?.let { criteriaBuilder.isMember(it.toPredicate(criteriaBuilder, itemRoot), itemRoot.get(field)) }
        )
    }
}

data class EmailExample(
    val type: StringFilter? = null,
    val email: StringFilter? = null,
) {
    fun <T> toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<T>): List<Predicate> {
        return listOfNotNull(
            type?.toPredicate(criteriaBuilder, itemRoot, Email_.TYPE),
            email?.toPredicate(criteriaBuilder, itemRoot, Email_.EMAIL),
        ).flatten()
    }
}