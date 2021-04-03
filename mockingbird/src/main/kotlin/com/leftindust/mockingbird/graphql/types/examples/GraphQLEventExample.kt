package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.leftindust.mockingbird.dao.entity.Visit
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLEventExample(
    val title: StringFilter? = null,
    val description: StringFilter? = null,
    val start: GraphQLTimeExample? = null,
    val end: GraphQLTimeExample? = null,
) : @GraphQLIgnore GraphQLExample<Visit> {
    override fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Visit>): List<Predicate> {
        TODO()
    }
}
