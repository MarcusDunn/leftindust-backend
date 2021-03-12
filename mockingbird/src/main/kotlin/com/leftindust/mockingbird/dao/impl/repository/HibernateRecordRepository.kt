package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.MediqRecord
import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable

interface HibernateRecordRepository : JpaRepository<MediqRecord, Long> {
    fun getByRid(rid: Int): MediqRecord?
    fun<PID: Serializable> getAllByPatientId(pid: PID): List<MediqRecord>
}
