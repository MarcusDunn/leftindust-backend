package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface HibernateEventRepository : JpaRepository<Event, Long> {
    fun findByTitleEquals(title: String): Event

    // TODO: 2021-04-13 make this not shit
    fun findAllByStartTimeAfterOrReoccurrenceIsNotNull(startTime: Timestamp): List<Event>
}