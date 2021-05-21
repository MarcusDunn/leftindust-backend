package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateVisitRepository : JpaRepository<Visit, Long> {
    fun getByEvent_Id(id: Long): Visit
    fun findByEvent_Id(id: Long): Visit?
}