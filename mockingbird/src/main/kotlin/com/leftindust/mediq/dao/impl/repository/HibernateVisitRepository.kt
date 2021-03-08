package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateVisitRepository : JpaRepository<Visit, Long> {
    fun getAllByPatientPid(pid: Int): List<Visit>
    fun getAllByDoctorDid(did: Int): List<Visit>
}