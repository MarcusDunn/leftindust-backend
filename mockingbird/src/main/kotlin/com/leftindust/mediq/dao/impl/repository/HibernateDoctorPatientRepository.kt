package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.DoctorPatient
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateDoctorPatientRepository : JpaRepository<DoctorPatient, Long> {
    fun <T> getAllByPatientId(patient_id: T): Set<DoctorPatient>
    fun <T> getAllByDoctorId(doctor_id: T): Set<DoctorPatient>
}