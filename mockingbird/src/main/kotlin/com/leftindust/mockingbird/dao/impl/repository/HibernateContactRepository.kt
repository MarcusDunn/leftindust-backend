package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.EmergencyContact
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateContactRepository : JpaRepository<EmergencyContact, Long> {
    fun getByCid(cid: Long): EmergencyContact?
}
