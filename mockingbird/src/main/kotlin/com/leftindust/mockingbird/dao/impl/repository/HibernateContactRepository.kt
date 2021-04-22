package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.EmergencyContact
import com.leftindust.mockingbird.dao.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateContactRepository : JpaRepository<EmergencyContact, Long> {
    fun getAllByPatient_Id(patient_id: Long): Collection<EmergencyContact>
}
