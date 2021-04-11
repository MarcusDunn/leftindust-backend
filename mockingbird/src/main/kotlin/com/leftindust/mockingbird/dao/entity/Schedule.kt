package com.leftindust.mockingbird.dao.entity

import java.sql.Timestamp
import javax.persistence.CascadeType
import javax.persistence.Embeddable
import javax.persistence.OneToMany

@Embeddable
class Schedule(
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "id")
    val events: MutableSet<Event> = emptySet<Event>().toMutableSet()
) {
    fun getEventsBetween(from: Timestamp, to: Timestamp): List<Event> {
        return this.events.filter { isBetween(from, it, to) }
    }

    private fun isBetween(
        from: Timestamp,
        event: Event,
        to: Timestamp
    ): Boolean {
        return from.before(event.startTime) && to.after(event.startTime)
    }

    fun addEvent(eventEntity: Event) {
        events.add(eventEntity)
    }
}

