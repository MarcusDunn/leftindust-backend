package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.extensions.getOrThrow
import com.leftindust.mockingbird.extensions.onUndefined
import com.leftindust.mockingbird.extensions.replaceAllIfNotNull
import com.leftindust.mockingbird.graphql.types.GraphQLUtcTime
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "event")
class Event(
    var title: String,
    var description: String?,
    @Column(name = "start_time")
    var startTime: Timestamp,
    @Column(name = "end_time")
    var endTime: Timestamp?,
    @Column(name = "all_day")
    var allDay: Boolean = false,
    @ManyToMany(mappedBy = "events")
    val doctors: MutableSet<Doctor>,
    @ManyToMany(mappedBy = "events")
    val patients: MutableSet<Patient>,
    @Embedded
    var reoccurrence: Reoccurrence? = null,
) : AbstractJpaPersistable() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Event

        if (title != other.title) return false
        if (description != other.description) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (allDay != other.allDay) return false
        if (doctors != other.doctors) return false
        if (patients != other.patients) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + allDay.hashCode()
        result = 31 * result + doctors.hashCode()
        result = 31 * result + patients.hashCode()
        return result
    }


    fun update(event: GraphQLEventEditInput, newDoctors: Set<Doctor>?, newPatients: Set<Patient>?) {
            title = event.title ?: title
            description = event.description.onUndefined(description)
            startTime = event.start?.toTimestamp() ?: startTime
            endTime = event.end.onUndefined(endTime?.time?.let { GraphQLUtcTime(it) })?.toTimestamp()
            allDay = event.allDay ?: allDay
            doctors.replaceAllIfNotNull(newDoctors?.toMutableSet() ?: mutableSetOf())// we call toMutableSet to avoid shared references to a collection
            patients.replaceAllIfNotNull(newPatients?.toMutableSet() ?: mutableSetOf())
            reoccurrence = if (event.recurrence is OptionalInput.Undefined)
                reoccurrence
            else
                event.recurrence.getOrThrow().let { Reoccurrence(it) }
    }

    constructor(graphQLEventInput: GraphQLEventInput, doctors: Set<Doctor>, patients: Set<Patient>) : this(
        title = graphQLEventInput.title,
        description = graphQLEventInput.description,
        startTime = graphQLEventInput.start.toTimestamp(),
        endTime = graphQLEventInput.end.toTimestamp(),
        allDay = graphQLEventInput.allDay,
        reoccurrence = graphQLEventInput.recurrence?.let { Reoccurrence(it) },
        doctors = doctors.toMutableSet(),
        patients = patients.toMutableSet()
    )

    fun clone(): Event {
        return Event(
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            allDay = allDay,
            doctors = doctors.toMutableSet(),
            patients = patients.toMutableSet(),
            reoccurrence = reoccurrence,
        )
    }
}