package com.leftindust.mockingbird.dao.entity

import com.leftindust.mockingbird.dao.entity.superclasses.AbstractJpaPersistable
import javax.persistence.*

@Entity(name = "clinic")
class Clinic(
    @OneToOne
    @JoinColumn(nullable = false)
    val address: Address,
    @OneToMany
    val doctors: Set<Doctor>,
) : AbstractJpaPersistable<Long>()