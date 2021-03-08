package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.DoctorPatient
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateDoctorPatientRepository: JpaRepository<DoctorPatient, Long> {
    fun getAllByPatientPid(pid: Int): Set<DoctorPatient>
    fun<T> getAllByPatientId(patient_id: T): Set<DoctorPatient>
    fun getAllByDoctorDid(did: Int): Set<DoctorPatient>
    fun<T> getAllByDoctorId(doctor_id: T): Set<DoctorPatient>
    fun getByPatientPidAndDoctorDid(pid: Int, did: Int): DoctorPatient?
}