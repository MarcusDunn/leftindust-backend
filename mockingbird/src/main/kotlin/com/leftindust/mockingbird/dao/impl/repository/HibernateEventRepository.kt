package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface HibernateEventRepository : JpaRepository<Event, Long> {
    fun getAllByTitleEquals(title: String): List<Event>

    fun findAllByStartTimeBeforeAndEndTimeAfterOrReoccurrenceIsNotNull(
        startTime: Timestamp,
        endTime: Timestamp
    ): List<Event>
}

fun HibernateEventRepository.getAllMatchingOrHasRecurrence(
    time: Timestamp
): List<Event> = this.findAllByStartTimeBeforeAndEndTimeAfterOrReoccurrenceIsNotNull(time, time)