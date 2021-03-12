package com.leftindust.mockingbird.dao.entity

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
)