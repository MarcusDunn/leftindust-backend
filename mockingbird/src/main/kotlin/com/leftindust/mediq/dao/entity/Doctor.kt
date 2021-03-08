package com.leftindust.mediq.dao.entity

import biweekly.component.VEvent
import biweekly.property.*
import biweekly.util.Duration
import com.leftindust.mediq.dao.entity.superclasses.Person
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
class Doctor(
    firstName: String,
    lastName: String,
    middleName: String? = null,
    dateOfBirth: Timestamp? = null,
    address: String? = null,
    email: String? = null,
    cellPhone: String? = null,
    workPhone: String? = null,
    homePhone: String? = null,
    @Column(name = "title", nullable = true)
    val title: String? = null,
    @Column(name = "doctor_id", nullable = false, unique = true)
    val did: Int,
    @Column(name = "pager_number", nullable = true)
    val pagerNumber: String? = null,
    @OneToMany(mappedBy = "doctor", cascade = [CascadeType.ALL])
    var patients: Set<DoctorPatient> = emptySet(),
    @Embedded
    val schedule: Schedule = Schedule()
) : Person(firstName, lastName, middleName, dateOfBirth, address, email, cellPhone, workPhone, homePhone) {

    fun addPatient(patient: Patient): Doctor {
        val doctorPatient = DoctorPatient(doctor = this, patient = patient)
        patient.doctors = patient.doctors.toMutableSet().apply { add(doctorPatient) }
        this.patients = this.patients.toMutableSet().apply { add(doctorPatient) }
        return this
    }

    fun getEventsBetween(from: Timestamp, to: Timestamp): List<MediqVEvent> {
        val utc = TimeZone.getTimeZone("UTC")
        return this.schedule.events.flatMap { event ->
            if (event.recurrenceRule == null) {
                if (isBetween(from, event, to))
                    sequenceOf(mediqEventAtDate(event, event.startTime))
                else
                    emptySequence()
            } else {
                event.recurrenceRule
                    .getDateIterator(latest(from.toDate(), event.startTime.toDate()), utc)
                    .iterator()
                    .asSequence()
                    .takeWhile { beforeOrEquals(it, to.toDate()) }
                    .map { mediqEventAtDate(event, it) }
            }
        }
    }

    private fun latest(rhs: Date, lhs: Date) = if (rhs.time > lhs.time) rhs else lhs

    private fun Timestamp.toDate() = Date.from(this.toInstant())

    private fun beforeOrEquals(it: Date, toDate: Date?) = it.before(toDate) || it == toDate

    private fun isBetween(
        from: Timestamp,
        event: Event,
        to: Timestamp,
    ) = from.time <= event.startTime.time && to.time >= (event.startTime + event.durationMillis).time

    private fun mediqEventAtDate(event: Event, it: Date): MediqVEvent {
        return MediqVEvent(this@Doctor).apply {
            summary = Summary(event.title)
            description = Description(event.description)
            dateStart = DateStart(it)
            dateEnd = DateEnd(it + event.durationMillis)
            dateTimeStamp = DateTimeStamp(it)
            duration = DurationProperty(Duration.fromMillis(event.durationMillis))
            id = event.id
        }
    }

    data class MediqVEvent(val doctor: Doctor) : VEvent()

    private operator fun Date.plus(startTime: Timestamp): Date {
        return Date((this.toInstant() + startTime.toInstant()).toEpochMilli())
    }

    private operator fun Date.plus(event: Event): Date {
        return Date.from(Instant.ofEpochMilli(this.toInstant().epochSecond * 1000 + event.durationMillis))
    }

    private operator fun Date.plus(durationMillis: Long): Date {
        return Date.from(Instant.ofEpochMilli(this.toInstant().epochSecond * 1000 + durationMillis))
    }

    private operator fun Instant.plus(toInstant: Instant): Instant {
        return Instant.ofEpochMilli(this.toEpochMilli() + toInstant.toEpochMilli())
    }
}










