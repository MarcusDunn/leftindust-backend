package com.leftindust.mediq.dao.impl.repository

import com.leftindust.mediq.dao.entity.AccessControlList
import com.leftindust.mediq.dao.entity.MediqGroup
import com.leftindust.mediq.dao.entity.MediqUser
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateAclRepository : JpaRepository<AccessControlList, Long> {
    fun findAllByMediqUser(user: MediqUser): List<AccessControlList>
    fun findAllByGroup(group: MediqGroup): List<AccessControlList>
}
