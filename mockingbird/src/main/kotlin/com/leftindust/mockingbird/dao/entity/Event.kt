package com.leftindust.mockingbird.dao.entity

import biweekly.property.RecurrenceRule
import com.leftindust.mockingbird.dao.entity.converters.RecurrenceConverter
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import java.sql.Timestamp
import javax.persistence.*


@Entity(name = "event")
class Event(
    val title: String,
    val description: String,
    @Column(name = "start_time")
    val startTime: Timestamp,
    @Column(name = "duration_millis")
    val durationMillis: Long,
    @Column(name = "recurrence_rule")
    @Convert(converter = RecurrenceConverter::class)
    val recurrenceRule: RecurrenceRule?
) : AbstractJpaPersistable<Long>() {

}