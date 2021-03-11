package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.MediqRecord
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateRecordRepository : JpaRepository<MediqRecord, Long> {
    fun getByRid(rid: Int): MediqRecord?
    fun<PID> getAllByPatientId(pid: PID): List<MediqRecord>
}
