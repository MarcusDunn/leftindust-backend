package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.graphql.types.GraphQLDayOfWeek
import com.leftindust.mockingbird.graphql.types.GraphQLRecurrence
import java.time.LocalDate
import javax.persistence.*

@Embeddable
class Reoccurrence(
    @Column(name = "start_date")
    val startDate: LocalDate,
    @Column(name = "end_date")
    val endDate: LocalDate,
    @ElementCollection
    @Enumerated(EnumType.STRING)
    val days: List<GraphQLDayOfWeek>,
) {
    constructor(reoccurrence: GraphQLRecurrence) : this(
        startDate = reoccurrence.startDate.toLocalDate(),
        endDate = reoccurrence.endDate.toLocalDate(),
        days = reoccurrence.daysOfWeek,
    )
}
