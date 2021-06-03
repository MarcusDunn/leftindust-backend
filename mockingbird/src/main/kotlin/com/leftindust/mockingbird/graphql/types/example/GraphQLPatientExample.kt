package com.leftindust.mockingbird.graphql.types.example

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

@GraphQLIgnore
interface PredicateInput {
    val strict: Boolean
    @GraphQLIgnore
    fun combineWithStrict(criteriaBuilder: CriteriaBuilder, vararg predicates: Predicate): Predicate {
        return if (strict) {
            criteriaBuilder.and(*predicates)
        } else {
            criteriaBuilder.or(*predicates)
        }
    }
}

@GraphQLIgnore
interface Example<T> : PredicateInput {
    @GraphQLIgnore
    fun toPredicate(criteriaBuilder: CriteriaBuilder, root: Root<T>): Predicate
}

@GraphQLIgnore
interface Filter<G> : PredicateInput {
    @GraphQLIgnore
    fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, G>
    ): Predicate
}

@GraphQLName("PatientExample")
data class GraphQLPatientExample(
    val firstName: StringFilter? = null,
    val lastName: StringFilter? = null,
    val dateOfBirth: DateFilter? = null,
    val insuranceNumber: StringFilter? = null,
    override val strict: Boolean,
): Example<Patient> {
    override fun toPredicate(criteriaBuilder: CriteriaBuilder, root: Root<Patient>): Predicate {
        val patientNameInfoJoin = root.join(Patient_.nameInfo)
        val predicates = listOfNotNull(
            firstName?.toPredicate(criteriaBuilder, patientNameInfoJoin, NameInfo_.firstName),
            lastName?.toPredicate(criteriaBuilder, patientNameInfoJoin, NameInfo_.lastName),
            dateOfBirth?.toPredicate(criteriaBuilder, root, Patient_.dateOfBirth),
            insuranceNumber?.toPredicate(criteriaBuilder, root, Patient_.insuranceNumber)
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *predicates)
    }
}

data class DateFilter(
    val before: GraphQLDateInput? = null,
    val after: GraphQLDateInput? = null,
    override val strict: Boolean,
): Filter<Date> {

    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, Date>
    ): Predicate {
        val toTypedArray = listOfNotNull(
            before?.let { criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), before.toDate()) },
            after?.let { criteriaBuilder.lessThanOrEqualTo(root.get(columnName), after.toDate()) },
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *toTypedArray)
    }
}

data class StringFilter(
    val eq: String? = null,
    val ne: String? = null,
    val contains: String? = null,
    val notContain: String? = null,
    val startsWith: String? = null,
    val notStartWith: String? = null,
    val endsWith: String? = null,
    val notEndWith: String? = null,
    override val strict: Boolean,
) : Filter<String> {

    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, String>
    ): Predicate {
        val column = criteriaBuilder.upper(root.get(columnName))
        val predicates = listOfNotNull(
            eq?.let { criteriaBuilder.equal(column, eq.uppercase()) },
            ne?.let { criteriaBuilder.notEqual(column, ne.uppercase()) },
            contains?.let { criteriaBuilder.like(column, "%$contains%".uppercase()) },
            notContain?.let { criteriaBuilder.notLike(column, "%$notContain%".uppercase()) },
            startsWith?.let { criteriaBuilder.like(column, "$startsWith%".uppercase()) },
            notStartWith?.let { criteriaBuilder.notLike(column, "$notStartWith%".uppercase()) },
            endsWith?.let { criteriaBuilder.like(column, "%$endsWith".uppercase()) },
            notEndWith?.let { criteriaBuilder.notLike(column, "%$notEndWith".uppercase()) },
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *predicates)
    }
}