package com.leftindust.mockingbird.graphql.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.Form
import com.leftindust.mockingbird.dao.entity.FormField
import com.leftindust.mockingbird.dao.entity.FormSection
import java.sql.Date

data class GraphQLFormTemplate(
    val name: String,
    val sections: List<GraphQLFormSection>,
    private val graphQLAuthContext: GraphQLAuthContext
) {
    constructor(form: Form, graphQLAuthContext: GraphQLAuthContext) : this(
        name = form.name,
        sections = form.sections
            .map { GraphQLFormSection(it, graphQLAuthContext) }
            .sortedBy { it.number },
        graphQLAuthContext = graphQLAuthContext
    )
}

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

data class GraphQlFormField(
    val dataType: FormField.DataType,
    val multiSelectPossibilities: List<String>?,
    val intUpperBound: Int?,
    val intLowerBound: Int?,
    val floatUpperBound: Int?,
    val floatLowerBound: Int?,
    val dateUpperBound: Date?,
    val dateLowerBound: Date?,
    val textRegex: String?,
    val jsonMetaData: String?,
    private val graphQLAuthContext: GraphQLAuthContext,
) {
    constructor(formField: FormField, graphQLAuthContext: GraphQLAuthContext) : this(
        dataType = formField.dataType,
        multiSelectPossibilities = formField.multiSelectPossibilities,
        intUpperBound = formField.intUpperBound,
        intLowerBound = formField.intLowerBound,
        floatUpperBound = formField.floatUpperBound,
        floatLowerBound = formField.floatLowerBound,
        dateUpperBound = formField.dateUpperBound,
        dateLowerBound = formField.dateLowerBound,
        textRegex = formField.textRegex,
        jsonMetaData = formField.jsonMetaData,
        graphQLAuthContext = graphQLAuthContext,
    )

    fun formSection(): GraphQLFormSection {
        TODO()
    }

}
