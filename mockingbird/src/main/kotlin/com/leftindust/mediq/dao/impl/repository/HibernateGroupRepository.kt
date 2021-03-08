package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.MediqGroup
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateGroupRepository : JpaRepository<MediqGroup, Long> {
    fun getByGid(gid: Long): MediqGroup?
}

