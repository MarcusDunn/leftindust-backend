package com.leftindust.mockingbird.graphql.types.example

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.dao.entity.NameInfo_
import com.leftindust.mockingbird.dao.entity.Patient
import com.leftindust.mockingbird.dao.entity.Patient_
import com.leftindust.mockingbird.graphql.types.input.GraphQLDateInput
import java.sql.Date
import javax.persistence.criteria.*
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
    val firstName: CaseAgnosticStringFilter? = null,
    val lastName: CaseAgnosticStringFilter? = null,
    val dateOfBirth: DateFilter? = null,
    val insuranceNumber: WhiteSpaceAgnosticStringFilter? = null,
    override val strict: Boolean,
) : Example<Patient> {
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
) : Filter<Date> {

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

data class FloatFilter(
    val gt: Int? = null,
    val lt: Int? = null,
    override val strict: Boolean,
) : Filter<Int> {
    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, Int>
    ): Predicate {
        val predicates = listOfNotNull(
            gt?.let { criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), gt) },
            lt?.let { criteriaBuilder.lessThanOrEqualTo(root.get(columnName), lt) },
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *predicates)
    }
}

data class IntFilter(
    val gt: Int? = null,
    val lt: Int? = null,
    val eq: Int? = null,
    val ne: Int? = null,
    override val strict: Boolean
) : Filter<Int> {
    override fun <Z, X> toPredicate(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, Int>
    ): Predicate {
        val predicates = listOfNotNull(
            gt?.let { criteriaBuilder.greaterThanOrEqualTo(root.get(columnName), gt) },
            lt?.let { criteriaBuilder.lessThanOrEqualTo(root.get(columnName), lt) },
            eq?.let { criteriaBuilder.equal(root.get(columnName), eq) },
            ne?.let { criteriaBuilder.notEqual(root.get(columnName), ne) },
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *predicates)
    }
}


class WhiteSpaceAgnosticStringFilter(
    eq: String? = null,
    ne: String? = null,
    contains: String? = null,
    notContain: String? = null,
    startsWith: String? = null,
    notStartWith: String? = null,
    endsWith: String? = null,
    notEndWith: String? = null,
    strict: Boolean,
) : AbstractStringFilter(
    eq = eq?.replace(" ", ""),
    ne = ne?.replace(" ", ""),
    contains = contains?.replace(" ", ""),
    notContain = notContain?.replace(" ", ""),
    startsWith = startsWith?.replace(" ", ""),
    notStartWith = notStartWith?.replace(" ", ""),
    endsWith = endsWith?.replace(" ", ""),
    notEndWith = notEndWith?.replace(" ", ""),
    strict = strict,
) {

    override fun <X, Z> editColumn(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, String>
    ): Expression<String> {
        return criteriaBuilder.function(
            "REPLACE",
            String::class.java,
            root.get(columnName),
            criteriaBuilder.literal(" "),
            criteriaBuilder.literal("")
        )
    }
}

class CaseAgnosticStringFilter(
    eq: String? = null,
    ne: String? = null,
    contains: String? = null,
    notContain: String? = null,
    startsWith: String? = null,
    notStartWith: String? = null,
    endsWith: String? = null,
    notEndWith: String? = null,
    strict: Boolean,
) : AbstractStringFilter(
    eq = eq?.uppercase(),
    ne = ne?.uppercase(),
    contains = contains?.uppercase(),
    notContain = notContain?.uppercase(),
    startsWith = startsWith?.uppercase(),
    notStartWith = notStartWith?.uppercase(),
    endsWith = endsWith?.uppercase(),
    notEndWith = notEndWith?.uppercase(),
    strict = strict,
) {

    override fun <X, Z> editColumn(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, String>
    ): Expression<String> {
        return criteriaBuilder.upper(root.get(columnName))
    }
}

abstract class AbstractStringFilter(
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
        val column = editColumn(criteriaBuilder, root, columnName)
        val predicates = listOfNotNull(
            eq?.let { criteriaBuilder.equal(column, eq) },
            ne?.let { criteriaBuilder.notEqual(column, ne) },
            contains?.let { criteriaBuilder.like(column, "%$contains%") },
            notContain?.let { criteriaBuilder.notLike(column, "%$notContain%") },
            startsWith?.let { criteriaBuilder.like(column, "$startsWith%") },
            notStartWith?.let { criteriaBuilder.notLike(column, "$notStartWith%") },
            endsWith?.let { criteriaBuilder.like(column, "%$endsWith") },
            notEndWith?.let { criteriaBuilder.notLike(column, "%$notEndWith") },
        ).toTypedArray()
        return combineWithStrict(criteriaBuilder, *predicates)
    }

    abstract fun <X, Z> editColumn(
        criteriaBuilder: CriteriaBuilder,
        root: From<Z, X>,
        columnName: SingularAttribute<X, String>
    ): Expression<String>
}