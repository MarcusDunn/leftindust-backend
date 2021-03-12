package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateDoctorRepository : JpaRepository<Doctor, Long>