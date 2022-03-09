package com.leftindust.mockingbird.graphql.types.input

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.leftindust.mockingbird.graphql.types.GraphQLMonth
import java.sql.Date
import java.time.LocalDate

@GraphQLName("DateInput")
data class GraphQLDateInput(
    val day: Int,
    val month: GraphQLMonth,
    val year: Int,
) {
    fun toLocalDate(): LocalDate = LocalDate.of(year, month.toJavaMonth(), day)

    fun toDate(): Date = Date.valueOf(LocalDate.of(year, month.toJavaMonth().value, day))
}
