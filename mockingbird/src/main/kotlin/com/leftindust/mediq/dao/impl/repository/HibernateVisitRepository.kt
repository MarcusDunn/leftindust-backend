package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

interface HibernateVisitRepository : JpaRepository<Visit, Long> {
    fun getAllByPatientId(pid: Long): List<Visit>
    fun <T: Serializable> getAllByDoctorId(doctor_id: T): List<Visit>
}