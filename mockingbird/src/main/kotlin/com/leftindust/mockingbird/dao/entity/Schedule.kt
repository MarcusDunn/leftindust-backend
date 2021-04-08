package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.graphql.types.GraphQLEvent
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Embeddable
import javax.persistence.OneToMany

@Embeddable
class Schedule(
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "id")
    val events: Set<Event> = emptySet()
) {
    fun getEventsBetween(from: Timestamp, to: Timestamp): List<GraphQLEvent.Unowned> {
        val utc = TimeZone.getTimeZone("UTC")
        return this.events.flatMap { event ->
            if (event.recurrenceRule == null) {
                if (isBetween(from, event, to))
                    sequenceOf(GraphQLEvent.Unowned(event))
                else
                    emptySequence()
            } else {
                event.recurrenceRule
                    .getDateIterator(event.startTime, utc)
                    .iterator()
                    .asSequence()
                    .filter { it.time > from.time }
                    .takeWhile { beforeOrEquals(it, to.toDate()) }
                    .map { GraphQLEvent.Unowned(event, it) }
            }
        }
    }

    private fun Timestamp.toDate() = Date.from(this.toInstant())

    private fun beforeOrEquals(it: Date, toDate: Date?) = it.before(toDate) || it == toDate

    private fun isBetween(
        from: Timestamp,
        event: Event,
        to: Timestamp,
    ) = from.time <= event.startTime.time && to.time >= (event.startTime + event.durationMillis).time

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