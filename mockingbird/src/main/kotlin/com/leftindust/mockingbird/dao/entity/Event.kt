package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "event")
class Event(
    val title: String,
    val description: String?,
    @Column(name = "start_time")
    var startTime: Timestamp,
    @Column(name = "duration_millis")
    val durationMillis: Long?,
    @Column(name = "all_day")
    val allDay: Boolean = GraphQLEventInput.allDayDefault,
    @ManyToMany
    @JoinTable(name = "event_doctor")
    val doctors: Set<Doctor>,
    @ManyToMany
    @JoinTable(name = "event_patient")
    val patients: Set<Patient>,
    @Embedded
    val reoccurrence: Reoccurrence? = null,
) : AbstractJpaPersistable<Long>() {

    init {
        if (allDay && durationMillis != null) {
            throw IllegalArgumentException("you cannot set `durationMillis` and `allDay`")
        } else if (!allDay && durationMillis == null) {
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
        if (durationMillis != other.durationMillis) return false
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
        result = 31 * result + durationMillis.hashCode()
        result = 31 * result + allDay.hashCode()
        result = 31 * result + doctors.hashCode()
        result = 31 * result + patients.hashCode()
        return result
    }

    override fun toString(): String {
        return "Event(title='$title', description=$description, startTime=$startTime, durationMillis=$durationMillis, allDay=$allDay, doctors=$doctors, patients=$patients)"
    }

    constructor(graphQLEventInput: GraphQLEventInput, doctors: Set<Doctor>, patients: Set<Patient>) : this(
        title = graphQLEventInput.title,
        description = graphQLEventInput.description,
        startTime = graphQLEventInput.start.toTimestamp(),
        durationMillis = graphQLEventInput.end?.toTimestamp()?.time?.minus(graphQLEventInput.start.toTimestamp().time),
        allDay = graphQLEventInput.allDay ?: GraphQLEventInput.allDayDefault,
        reoccurrence = graphQLEventInput.reoccurrence?.let { Reoccurrence(it) },
        doctors = doctors,
        patients = patients
    )
}