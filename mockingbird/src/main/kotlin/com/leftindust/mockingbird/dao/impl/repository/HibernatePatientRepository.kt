package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
interface HibernatePatientRepository : JpaRepository<Patient, UUID> {
    fun getPatientsById(id: UUID): Collection<Patient>
    fun findByUser_UniqueId(user_uniqueId: String): Patient?
}