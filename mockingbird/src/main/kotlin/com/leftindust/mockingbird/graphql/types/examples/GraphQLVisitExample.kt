package com.leftindust.mockingbird.graphql.types.examples

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.leftindust.mockingbird.dao.entity.Visit
import com.leftindust.mockingbird.dao.entity.Visit_
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

data class GraphQLVisitExample(
    val vid: StringFilter? = null,
    val event: GraphQLEventExample? = null,
    val title: StringFilter? = null,
    val description: StringFilter? = null,
    val icdFoundationCode: StringFilter? = null,
) : @GraphQLIgnore GraphQLExample<Visit> {

    @GraphQLIgnore
    override fun toPredicate(criteriaBuilder: CriteriaBuilder, itemRoot: Root<Visit>): List<Predicate> {
        return listOfNotNull(
            vid?.toPredicate(criteriaBuilder, itemRoot, Visit_.ID),
            event?.toPredicate(criteriaBuilder, itemRoot),
            title?.toPredicate(criteriaBuilder, itemRoot, Visit_.TITLE),
            description?.toPredicate(criteriaBuilder, itemRoot, Visit_.DESCRIPTION),
            icdFoundationCode?.toPredicate(criteriaBuilder, itemRoot, "icdFoundationCode"),
        ).flatten()
    }
}