package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateDoctorRepository : JpaRepository<Doctor, Long>