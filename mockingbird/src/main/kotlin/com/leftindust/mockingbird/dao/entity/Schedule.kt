package com.leftindust.mockingbird.dao.entity

import biweekly.component.VEvent
import biweekly.property.*
import biweekly.util.Duration
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.OneToMany

@Embeddable
class Schedule(
    @Column(name = "schedule_id")
    val scheduleId: Long? = null,
    @OneToMany(cascade = [CascadeType.ALL])
    val events: Set<Event> = emptySet()
) {
    fun getEventsBetween(from: Timestamp, to: Timestamp): List<VEvent> {
        val utc = TimeZone.getTimeZone("UTC")
        return this.events.flatMap { event ->
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

    private fun mediqEventAtDate(event: Event, it: Date): VEvent {
        return VEvent().apply {
            summary = Summary(event.title)
            description = Description(event.description)
            dateStart = DateStart(it)
            dateEnd = DateEnd(it + event.durationMillis)
            dateTimeStamp = DateTimeStamp(it)
            duration = DurationProperty(Duration.fromMillis(event.durationMillis))
        }
    }

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