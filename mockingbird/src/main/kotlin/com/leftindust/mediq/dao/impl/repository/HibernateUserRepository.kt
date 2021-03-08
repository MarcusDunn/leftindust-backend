package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.MediqUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
interface HibernateUserRepository: JpaRepository<MediqUser, Long> {
    fun getUserByUniqueId(uniqueId: String): MediqUser?
}