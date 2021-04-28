package com.leftindust.mockingbird.dao.impl.repository

import com.leftindust.mockingbird.dao.entity.NameInfo
import org.springframework.data.jpa.repository.JpaRepository

interface HibernateNameInfoRepository: JpaRepository<NameInfo, Long>
