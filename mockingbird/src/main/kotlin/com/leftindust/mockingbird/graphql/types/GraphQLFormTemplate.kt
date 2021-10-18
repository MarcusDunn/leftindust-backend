package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Form
import java.util.*

data class GraphQLFormTemplate(
    val id: ID,
    val name: String,
    val sections: List<GraphQLFormSection>,
    private val graphQLAuthContext: GraphQLAuthContext
) {
    @GraphQLName("FormTemplateId")
    data class ID(val id: UUID)

    constructor(form: Form, graphQLAuthContext: GraphQLAuthContext) : this(
        id = ID(form.id!!),
        name = form.name,
        sections = form.sections
            .map { GraphQLFormSection(it, graphQLAuthContext) }
            .sortedBy { it.number },
        graphQLAuthContext = graphQLAuthContext
    )
}

