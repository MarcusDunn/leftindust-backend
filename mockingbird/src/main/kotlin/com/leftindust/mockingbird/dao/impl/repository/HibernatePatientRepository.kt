package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("FunctionName")
interface HibernatePatientRepository : JpaRepository<Patient, UUID> {
    fun findByUser_UniqueId(user_uniqueId: String): Patient?
}