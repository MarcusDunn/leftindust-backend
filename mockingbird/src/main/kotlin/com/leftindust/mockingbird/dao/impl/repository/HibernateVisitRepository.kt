package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface HibernateVisitRepository : JpaRepository<Visit, UUID> {
    fun findByEvent_Id(event_id: UUID): Visit?
}