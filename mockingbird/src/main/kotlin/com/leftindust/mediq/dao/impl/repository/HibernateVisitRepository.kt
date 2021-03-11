package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

interface HibernateVisitRepository : JpaRepository<Visit, Long> {
    fun getAllByPatientPid(pid: Int): List<Visit>
    fun <T> getAllByDoctorId(doctor_id: T): List<Visit>
}