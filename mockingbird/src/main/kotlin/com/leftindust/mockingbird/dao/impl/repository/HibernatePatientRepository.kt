package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
interface HibernatePatientRepository : JpaRepository<Patient, Long> {
    fun getAllByFirstNameLikeOrMiddleNameLikeOrLastNameLike(
        firstName: String,
        middleName: String,
        lastName: String
    ): List<Patient>
}