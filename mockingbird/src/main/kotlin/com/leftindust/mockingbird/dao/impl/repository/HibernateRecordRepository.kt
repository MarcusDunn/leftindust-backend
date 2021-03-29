package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.MediqRecord
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

interface HibernateRecordRepository : JpaRepository<MediqRecord, Long> {
    fun getAllByPatientId(pid: Long): List<MediqRecord>
}
