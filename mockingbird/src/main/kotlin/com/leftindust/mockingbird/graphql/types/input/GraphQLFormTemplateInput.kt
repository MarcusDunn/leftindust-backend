package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.graphql.types.DataType

@GraphQLName("FormTemplateInput")
data class GraphQLFormTemplateInput(
    val name: String,
    val sections: List<GraphQLFormSectionInput>,
)

@GraphQLName("FormSectionInput")
data class GraphQLFormSectionInput(
    val name: String,
    val number: Int,
    @GraphQLDescription("Max 50 000 chars")
    val description: String? = null,
    @GraphQLDescription("Note that I do not provide a stable order to these fields")
    val fields: List<GraphQlFormFieldInput>,
)

@GraphQLName("FormFieldInput")
data class GraphQlFormFieldInput(
    val title: String,
    val dataType: DataType,
    val number: Int,
    val multiSelectPossibilities: List<String>? = null,
    val intUpperBound: Int? = null,
    val intLowerBound: Int? = null,
    val floatUpperBound: Float? = null,
    val floatLowerBound: Float? = null,
    val dateUpperBound: GraphQLDateInput? = null,
    val dateLowerBound: GraphQLDateInput? = null,
    val textRegex: String? = null,
    val jsonMetaData: String? = null,
) {
    init {
        val valid = when (dataType) {
            DataType.SingleMuliSelect -> multiSelectPossibilities != null
            DataType.MultiMuliSelect -> multiSelectPossibilities != null
            else -> true
        }
        if (!valid) {
            throw IllegalArgumentException("invalid form")
        }
    }
}

