package com.leftindust.mockingbird.graphql.types.example

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.dao.entity.NameInfo_
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Join
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.metamodel.SingularAttribute

interface Example<T> {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, root: Root<T>): Predicate
}

interface Filter {
    fun <T> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: Root<T>,
        columnName: SingularAttribute<T, String>?
    ): Predicate

    fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: Join<Z, X>,
        columnName: SingularAttribute<X, String>?
    ): Predicate
}

@GraphQLName("PatientExample")
data class GraphQLPatientExample(
    val firstName: StringFiler? = null,
    val lastName: StringFiler? = null,
    @GraphQLDescription("weather to connect multiple parameters with and (strict) or or (non-strict)")
    val strict: Boolean = true,
) : @GraphQLIgnore Example<Patient> {
    @GraphQLIgnore
    override fun toPredicate(criteriaBuilder: CriteriaBuilder, root: Root<Patient>): Predicate {
        val toTypedArray = predicates(criteriaBuilder, root).toTypedArray()
        return if (strict) {
            criteriaBuilder.and(*toTypedArray)
        } else {
            criteriaBuilder.or(*toTypedArray)
        }
    }

    private fun predicates(criteriaBuilder: CriteriaBuilder, root: Root<Patient>): List<Predicate> {
        val patientNameJoin = root.join(Patient_.nameInfo)
        return listOfNotNull(
            firstName?.toPredicate(criteriaBuilder, patientNameJoin, NameInfo_.firstName),
            lastName?.toPredicate(criteriaBuilder, patientNameJoin, NameInfo_.lastName)
        )
    }
}

data class StringFiler(
    val eq: String? = null,
    @GraphQLDescription("weather to connect multiple filters with and (strict) or or (non-strict)")
    val strict: Boolean = true,
) : @GraphQLIgnore Filter {

    @GraphQLIgnore
    override fun <T> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: Root<T>,
        columnName: SingularAttribute<T, String>?
    ): Predicate {
        val toTypedArray = listOfNotNull(criteriaBuilder.equal(root.get(columnName), eq)).toTypedArray()
        return if (strict) {
            criteriaBuilder.and(*toTypedArray)
        } else {
            criteriaBuilder.or(*toTypedArray)
        }
    }

    @GraphQLIgnore
    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: Join<Z, X>,
        columnName: SingularAttribute<X, String>?
    ): Predicate {
        val toTypedArray = listOfNotNull(criteriaBuilder.equal(root.get(columnName), eq)).toTypedArray()
        return if (strict) {
            criteriaBuilder.and(*toTypedArray)
        } else {
            criteriaBuilder.or(*toTypedArray)
        }
    }
}