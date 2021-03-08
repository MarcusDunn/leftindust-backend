package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.EmergencyContact
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateContactRepository : JpaRepository<EmergencyContact, Long> {
    fun getByCid(cid: Long): EmergencyContact?
}
