package com.leftindust.mockingbird.graphql.types.input

import com.leftindust.mockingbird.graphql.types.GraphQLMonth
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId

data class GraphQLDateInput(
    val day: Int,
    val month: GraphQLMonth,
    val year: Int,
) {
    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month.toJavaMonth(), day)
    }

    fun toTimeStamp(): Timestamp {
        return Timestamp.from(toLocalDate().atStartOfDay(ZoneId.of("UTC")).toInstant())
    }
}
