package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Clinic
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateClinicRepository : JpaRepository<Clinic, Long> {

}
