package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Clinic
import com.leftindust.mockingbird.dao.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateClinicRepository : JpaRepository<Clinic, Long> {
    fun getAllByDoctors(doctors: MutableSet<Doctor>): Collection<Clinic>
}
