package com.leftindust.mockingbird.graphql.types

import com.leftindust.mockingbird.auth.GraphQLAuthContext
import com.leftindust.mockingbird.dao.entity.FormField
import java.sql.Date

data class GraphQlFormField(
    val dataType: FormField.DataType,
    val number: Int,
    val multiSelectPossibilities: List<String>?,
    val intUpperBound: Int?,
    val intLowerBound: Int?,
    val floatUpperBound: Float?,
    val floatLowerBound: Float?,
    val dateUpperBound: Date?,
    val dateLowerBound: Date?,
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
        dateUpperBound = formField.dateUpperBound,
        dateLowerBound = formField.dateLowerBound,
        textRegex = formField.textRegex,
        jsonMetaData = formField.jsonMetaData,
        graphQLAuthContext = graphQLAuthContext,
    )
}