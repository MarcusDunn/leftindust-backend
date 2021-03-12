package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.AccessControlList
import com.leftindust.mockingbird.dao.entity.MediqGroup
import com.leftindust.mockingbird.dao.entity.MediqUser
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateAclRepository : JpaRepository<AccessControlList, Long> {
    fun findAllByMediqUser(user: MediqUser): List<AccessControlList>
    fun findAllByGroup(group: MediqGroup): List<AccessControlList>
}
