package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import com.leftindust.mockingbird.graphql.types.input.GraphQLEventInput
import java.sql.Timestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity(name = "event")
class Event(
    val title: String,
    val description: String?,
    @Column(name = "start_time")
    var startTime: Timestamp,
    @Column(name = "duration_millis")
    val durationMillis: Long,
    @ManyToMany
    @JoinTable(name = "event_doctor")
    val doctors: Set<Doctor>,
    @ManyToMany
    @JoinTable(name = "event_patient")
    val patients: Set<Patient>,
) : AbstractJpaPersistable<Long>() {
    fun atDate(date: Date): Event {
        return Event(
            title,
            description,
            startTime = Timestamp.from(date.toInstant()),
            durationMillis,
            doctors,
            patients
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (title != other.title) return false
        if (description != other.description) return false
        if (startTime != other.startTime) return false
        if (durationMillis != other.durationMillis) return false
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
        result = 31 * result + doctors.hashCode()
        result = 31 * result + patients.hashCode()
        return result
    }

    override fun toString(): String {
        return "Event(title='$title', description=$description, startTime=$startTime, durationMillis=$durationMillis, doctors=$doctors, patients=$patients)"
    }

    // this is for one off events and as such has no recurrence rule
    constructor(graphQLEventInput: GraphQLEventInput, doctors: Set<Doctor>, patients: Set<Patient>) : this(
        title = graphQLEventInput.title,
        description = graphQLEventInput.description,
        startTime = graphQLEventInput.start.toTimestamp(),
        durationMillis = graphQLEventInput.end.toTimestamp().time - graphQLEventInput.start.toTimestamp().time,
        doctors,
        patients
    )
}