package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface HibernateClinicRepository : JpaRepository<Clinic, UUID> {
    fun getAllByDoctorsContains(doctor: Doctor): Collection<Clinic>
}
