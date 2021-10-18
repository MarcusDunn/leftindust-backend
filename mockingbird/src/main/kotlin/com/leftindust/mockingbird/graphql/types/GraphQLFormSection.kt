package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.FormSection

data class GraphQLFormSection(
    val name: String,
    val number: Int,
    @GraphQLDescription("Note that I do not provide a stable order to these fields")
    val fields: List<GraphQlFormField>,
    private val graphQLAuthContext: GraphQLAuthContext,
) {
    constructor(section: FormSection, graphQLAuthContext: GraphQLAuthContext) : this(
        name = section.name,
        number = section.number,
        fields = section.fields.map { GraphQlFormField(it, graphQLAuthContext) },
        graphQLAuthContext = graphQLAuthContext,
    )
}