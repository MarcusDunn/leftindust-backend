package com.leftindust.mockingbird.dao.entity

import biweekly.property.RecurrenceRule
import com.leftindust.mockingbird.dao.entity.converters.RecurrenceConverter
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity

@Entity(name = "event")
class Event(
    val title: String,
    val description: String?,
    @Column(name = "start_time")
    val startTime: Timestamp,
    @Column(name = "duration_millis")
    val durationMillis: Long,
    @Column(name = "recurrence_rule")
    @Convert(converter = RecurrenceConverter::class)
    val recurrenceRule: RecurrenceRule?
) : AbstractJpaPersistable<Long>() {
    // this is for one off events and as such has no recurrence rule
    constructor(graphQLEventInput: GraphQLEventInput) : this(
        title = graphQLEventInput.title,
        description = graphQLEventInput.description,
        startTime = graphQLEventInput.start.toTimestamp(),
        durationMillis = graphQLEventInput.end.toTimestamp().time - graphQLEventInput.start.toTimestamp().time,
        recurrenceRule = null,
    )
}