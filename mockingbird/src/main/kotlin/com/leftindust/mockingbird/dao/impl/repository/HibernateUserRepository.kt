package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.MediqUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
interface HibernateUserRepository: JpaRepository<MediqUser, Long> {
    fun getByUniqueId(uniqueId: String): MediqUser
    fun findByUniqueId(uniqueId: String): MediqUser?
}