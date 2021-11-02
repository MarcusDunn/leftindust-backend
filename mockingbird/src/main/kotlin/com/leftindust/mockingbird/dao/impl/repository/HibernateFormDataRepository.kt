package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.FormData
import com.leftindust.mockingbird.dao.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface HibernateFormDataRepository : JpaRepository<FormData, UUID> {
    fun getByPatient_Id(patient_id: UUID): List<FormData>
}