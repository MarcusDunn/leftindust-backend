package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.Visit
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLVisitExample(
    val vid: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val timeBooked: OptionalInput<GraphQLTimeExample> = OptionalInput.Undefined,
    val timeOfVisit: OptionalInput<GraphQLTimeExample> = OptionalInput.Undefined,
    val title: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val description: OptionalInput<StringFilter> = OptionalInput.Undefined,
    val doctor: OptionalInput<GraphQLDoctorExample> = OptionalInput.Undefined,
    val patient: OptionalInput<GraphQLPatientExample> = OptionalInput.Undefined,
    val icdFoundationCode: OptionalInput<StringFilter> = OptionalInput.Undefined,
) : @GraphQLIgnore GraphQLExample<Visit> {

    @GraphQLIgnore
    override fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Visit>): List<Predicate> {
        TODO()
    }
}