package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.FormField

data class GraphQlFormField(
    val dataType: DataType,
    val number: Int,
    val multiSelectPossibilities: List<String>?,
    val intUpperBound: Int?,
    val intLowerBound: Int?,
    val floatUpperBound: Float?,
    val floatLowerBound: Float?,
    val dateUpperBound: GraphQLDate?,
    val dateLowerBound: GraphQLDate?,
    val textRegex: String?,
    val jsonMetaData: String?,
    private val graphQLAuthContext: GraphQLAuthContext,
) {
    constructor(formField: FormField, graphQLAuthContext: GraphQLAuthContext) : this(
        number = formField.number,
        dataType = formField.dataType,
        multiSelectPossibilities = formField.multiSelectPossibilities,
        intUpperBound = formField.intUpperBound,
        intLowerBound = formField.intLowerBound,
        floatUpperBound = formField.floatUpperBound,
        floatLowerBound = formField.floatLowerBound,
        dateUpperBound = formField.dateUpperBound?.toLocalDate()?.let { GraphQLDate(it) },
        dateLowerBound = formField.dateLowerBound?.toLocalDate()?.let { GraphQLDate(it) },
        textRegex = formField.textRegex,
        jsonMetaData = formField.jsonMetaData,
        graphQLAuthContext = graphQLAuthContext,
    )
}