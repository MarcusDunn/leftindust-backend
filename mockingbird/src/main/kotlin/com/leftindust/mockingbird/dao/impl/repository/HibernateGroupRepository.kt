package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.MediqGroup
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateGroupRepository : JpaRepository<MediqGroup, Long>

