package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

interface HibernateVisitRepository : JpaRepository<Visit, Long> {
    fun getAllByPatientId(pid: Long): List<Visit>
    fun getAllByDoctorId(did: Long): List<Visit>
}