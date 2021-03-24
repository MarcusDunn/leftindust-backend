package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.Entity

@Entity(name = "mediq_group")
class MediqGroup(
    val name: String,
) : AbstractJpaPersistable<Long>()