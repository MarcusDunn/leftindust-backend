package com.leftindust.mockingbird.dao.entity

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.extensions.getOrThrow
import com.leftindust.mockingbird.extensions.onUndefined
import com.leftindust.mockingbird.graphql.types.GraphQLTimeInput
import com.leftindust.mockingbird.graphql.types.GraphQLUtcTime
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventEditInput
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "event")
class Event(
    val title: String,
    val description: String?,
    @Column(name = "start_time")
    var startTime: Timestamp,
    @Column(name = "end_time")
    val endTime: Timestamp?,
    @Column(name = "all_day")
    val allDay: Boolean = false,
    @ManyToMany
    @JoinTable(name = "event_doctor")
    val doctors: Set<Doctor>,
    @ManyToMany
    @JoinTable(name = "event_patient")
    val patients: Set<Patient>,
    @Embedded
    var reoccurrence: Reoccurrence? = null,
) : AbstractJpaPersistable<Long>() {

    init { // validation logic
        if (allDay && endTime != null) {
            throw IllegalArgumentException("you cannot set `durationMillis` and `allDay`")
        } else if (!allDay && endTime == null) {
            throw IllegalArgumentException("you must set `startTime/durationMillis` or `allDay`")
        }
    }

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


    fun update(event: GraphQLEventEditInput, newDoctors: Set<Doctor>?, newPatients: Set<Patient>?): Event {
        return Event(
            title = event.title ?: title,
            description = event.description.onUndefined(description),
            startTime = event.start?.toTimestamp() ?: startTime,
            endTime = event.end.onUndefined(endTime?.time?.let { GraphQLTimeInput(GraphQLUtcTime(it)) })?.toTimestamp(),
            allDay = event.allDay ?: allDay,
            doctors = newDoctors ?: doctors, // we call toMutableSet to avoid shared references to a collection
            patients = newPatients ?: patients,
            reoccurrence = if (event.recurrence is OptionalInput.Undefined)
                reoccurrence
            else
                event.recurrence.getOrThrow().let { Reoccurrence(it) }
        )
    }

    constructor(graphQLEventInput: GraphQLEventInput, doctors: Set<Doctor>, patients: Set<Patient>) : this(
        title = graphQLEventInput.title,
        description = graphQLEventInput.description,
        startTime = graphQLEventInput.start.toTimestamp(),
        endTime = graphQLEventInput.end?.toTimestamp(),
        allDay = graphQLEventInput.allDay ?: false,
        reoccurrence = graphQLEventInput.recurrence?.let { Reoccurrence(it) },
        doctors = doctors,
        patients = patients
    )

    fun clone(): Event {
        return Event(
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            allDay = allDay,
            // toMutableSet clones the collection to avoid shared references to a collection
            doctors = doctors.toMutableSet(),
            // toMutableSet clones the collection to avoid shared references to a collection
            patients = patients.toMutableSet(),
            reoccurrence = reoccurrence,
        )
    }

    override fun toString(): String {
        return "Event(title='$title', description=$description, startTime=$startTime, endTime=$endTime, allDay=$allDay, doctors=$doctors, patients=$patients, reoccurrence=$reoccurrence)"
    }
}