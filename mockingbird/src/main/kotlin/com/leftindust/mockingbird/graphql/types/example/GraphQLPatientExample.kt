package com.leftindust.mockingbird.graphql.types.example

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.dao.entity.NameInfo_
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import java.sql.Date
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.From
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.metamodel.SingularAttribute

interface Example<T> {
    fun toPredicate(criteriaBuilder: CriteriaBuilder, root: Root<T>): Predicate
}

interface Filter<G> {
    fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, G>
    ): Predicate
}

@GraphQLName("PatientExample")
data class GraphQLPatientExample(
    val firstName: StringFiler? = null,
    val lastName: StringFiler? = null,
    val dateOfBirth: DateFilter? = null,
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
            lastName?.toPredicate(criteriaBuilder, patientNameJoin, NameInfo_.lastName),
            dateOfBirth?.toPredicate(criteriaBuilder, root, Patient_.dateOfBirth)
        )
    }
}

data class DateFilter(
    val before: GraphQLDateInput? = null,
    val after: GraphQLDateInput? = null,
    val strict: Boolean,
) : @GraphQLIgnore Filter<Date> {

    @GraphQLIgnore
    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, Date>
    ): Predicate {
        val toTypedArray = listOfNotNull(
            before?.let { criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), before.toDate()) },
            after?.let { criteriaBuilder.lessThanOrEqualTo(root.get(columnName), after.toDate()) },
        ).toTypedArray()
        return if (strict) {
            criteriaBuilder.and(*toTypedArray)
        } else {
            criteriaBuilder.or(*toTypedArray)
        }
    }
}

data class StringFiler(
    val eq: String? = null,
    val ne: String? = null,
    val contains: String? = null,
    val notContain: String? = null,
    val startsWith: String? = null,
    val notStartWith: String? = null,
    val endsWith: String? = null,
    val notEndWith: String? = null,
    @GraphQLDescription("weather to connect multiple filters with and (strict) or or (non-strict)")
    val strict: Boolean = true,
) : @GraphQLIgnore Filter<String> {

    @GraphQLIgnore
    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, String>
    ): Predicate {
        val column = root.get(columnName)
        val toTypedArray = listOfNotNull(
            eq?.let { criteriaBuilder.equal(column, eq) },
            ne?.let { criteriaBuilder.notEqual(column, ne) },
            contains?.let { criteriaBuilder.like(column, "%$contains%") },
            notContain?.let { criteriaBuilder.notLike(column, "%$notContain%") },
            startsWith?.let { criteriaBuilder.equal(column, "$startsWith%") },
            notStartWith?.let { criteriaBuilder.notLike(column, "$notStartWith%") },
            endsWith?.let { criteriaBuilder.equal(column, "%$endsWith") },
            notEndWith?.let { criteriaBuilder.notLike(column, "%$notEndWith") },
        ).toTypedArray()
        return if (strict) {
            criteriaBuilder.and(*toTypedArray)
        } else {
            criteriaBuilder.or(*toTypedArray)
        }
    }
}